package com.example.user.bluetooth_communication.ui;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.R;
import com.example.user.bluetooth_communication.databinding.ActivityHomeBinding;
import com.example.user.bluetooth_communication.remote.AppUtils;
import com.example.user.bluetooth_communication.remote.Model.Request.AdduserReq;
import com.example.user.bluetooth_communication.remote.Model.Response.AddUserRes;
import com.example.user.bluetooth_communication.remote.Model.Response.GetAllUser;
import com.example.user.bluetooth_communication.remote.Model.Response.UserInfo;
import com.example.user.bluetooth_communication.remote.SharedPref;
import com.example.user.bluetooth_communication.remote.UserService;
import com.example.user.bluetooth_communication.ui.Utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PairedHomeActivity extends AppCompatActivity {
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static Handler handler;
    private static BluetoothSocket mmSocket;
    public UserService mService = AppUtils.mService();
    private static ConnectedThread connectedThread;
    private static CreateConnectThread createConnectThread;
    String accessToken;
    public GetAllUserAdapter mUserAdapter;
    private RecyclerView userRecyclerView;
    public ArrayList<UserInfo> userList = new ArrayList<>();
    private String deviceName = null;
    private String deviceAddress;
    LayoutInflater layoutInflater;
    AlertDialog alert;
    View inflator;
    private LinearLayout linearLayout;
    String mLastname;
    TextView mText;
    ProgressDialog mProgressDialog;
    private Button sendOpen;
    private String mFirstName;
    private ActivityHomeBinding binding;
    EditText searchEdit;
    CardView mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        accessToken = SharedPref.getString(Constants.TOKEN, "");
        Log.i("tok", accessToken);
        userList = new ArrayList<>();
        userRecyclerView = findViewById(R.id.deleteLists);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchEdit = findViewById(R.id.searchEdit);
        mCardView = findViewById(R.id.cardView);
        mText = findViewById(R.id.textVisible);
        binding.btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes;
                String btnOpen = binding.btnOpen.getText().toString().toUpperCase(Locale.ROOT);
                bytes = btnOpen.getBytes();
                connectedThread.write(bytes);
            }
        });


        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null) {
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            binding.toolbar.setSubtitle("Connecting to " + deviceName + "...");
            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 6:
                                binding.toolbar.setSubtitle("Connected to " + deviceName);
                                mCardView.setVisibility(View.VISIBLE);
                                mDeleteUser(accessToken);
                                break;
                            case -1:
                                binding.toolbar.setSubtitle("Device fails to connect");
                                break;
                        }
                        break;

                    case Constants.MESSAGE_READ:
                        String arduinoMsg = String.valueOf(msg.obj);
                        Log.i("nnnnuu", arduinoMsg);
                        try {
                            String[] parts = arduinoMsg.split(":");
                            Log.i("nnnnkk", parts[0]);
                            switch (parts[0]) {
                                case "place_finger":
                                    Toast.makeText(getApplicationContext(), "Ask user to place finger", Toast.LENGTH_SHORT).show();
                                    break;
                                case "MSG_OK":
                                    Toast.makeText(getApplicationContext(), "MSG_OK", Toast.LENGTH_SHORT).show();
                                    break;
                                case "remove_finger":
                                    Toast.makeText(getApplicationContext(), "Ask user to remove finger", Toast.LENGTH_SHORT).show();
                                    break;
                                case "communication_error":
                                case "imaging_error":
                                case "unknown_error":
                                case "memory_error":
                                case "prints_did_not_match":
                                    Toast.makeText(getApplicationContext(), "start process again",  Toast.LENGTH_SHORT).show();
                                    break;
                                case "place_finger_again":
                                    Toast.makeText(getApplicationContext(), "Ask user to place same finger again",  Toast.LENGTH_SHORT).show();
                                    break;
                                case "prints_saved_with_id":
                                    alert.dismiss();
                                    addUser(mFirstName, mLastname, parts[1], accessToken);
                                    Toast.makeText(getApplicationContext(), "User saved successfully", Toast.LENGTH_SHORT).show();
                                    mDeleteUser(accessToken);
                                    break;
                                case "BATTERY":
                                    Toast.makeText(getApplicationContext(), "Battery %",  Toast.LENGTH_SHORT).show();
                                    break;
                                case "SYNC_YES":
                                    Log.i("user", arduinoMsg);
                                    mGetUser(accessToken);
                                    break;
                                case "DELETE":
                                    mUserAdapter.removeItem(parts[1]);
                                    deleteUser(accessToken, parts[1]);
                            }

                        } catch (Exception e) {
                            Log.i("jjj", e.getLocalizedMessage());
                        }
                        break;
                }
            }
        };
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
              //  Log.i(TAG, "onTextChanged: hhhh");
                Log.i("jjj",charSequence.toString());
                mUserAdapter.getFilter().filter(charSequence.toString());
                if (mUserAdapter.getItemCount() == 0) {
                    mText.setVisibility(View.VISIBLE);
                } else {
                    mText.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.i(TAG, "afterTextChanged: kkkk");
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes;
                String btnAdd = "SYNC_?";
                bytes = btnAdd.getBytes();
                connectedThread.write(bytes);
                layoutInflater = LayoutInflater.from(PairedHomeActivity.this);
                inflator = layoutInflater.inflate(R.layout.add_user_layout, null);
                alert = new AlertDialog.Builder(PairedHomeActivity.this).create();

                alert.setTitle("Add User");
                alert.setView(inflator);
                alert.setCancelable(true);

                final EditText firstName = (EditText) inflator.findViewById(R.id.firstName);
                final EditText lastName = (EditText) inflator.findViewById(R.id.lastName);
                final Button mBtn = inflator.findViewById(R.id.btnAdd);
                mBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (firstName.getText().toString().isEmpty() && lastName.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Field is Required", Toast.LENGTH_SHORT).show();
                        } else {
                            mFirstName = firstName.getText().toString();
                            mLastname = lastName.getText().toString();
                            Log.i("hhhhh", mFirstName + mLastname);
                            byte[] bytes;
                            String btnAdd = "ADD";
                            bytes = btnAdd.getBytes();
                            connectedThread.write(bytes);
                        }
                    }
                });
                alert.show();
            }
        });
    }



    private void mGetUser(String token) {
        mService.syncUsers(token).enqueue(new Callback<GetAllUser>() {
            @Override
            public void onResponse(Call<GetAllUser> call, Response<GetAllUser> response) {
                // mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    GetAllUser resp = response.body();
                    String id = response.body().getData().getIdDevice();
                    Log.i(TAG, id);
                    if (id.isEmpty()) {
                        Log.i(TAG, "no id found");
                    } else {
                        byte[] bytes;
                        String sendIdOnDevice = id;
                        bytes = sendIdOnDevice.getBytes();
                        connectedThread.write(bytes);
                    }

                    Log.i("success", "onResponse: " + resp);
                    //Log.i("success", user);

                } else {
                    Log.i("error", "onResponse: not successful");
                }

            }

            @Override
            public void onFailure(Call<GetAllUser> call, Throwable t) {
                //mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
                if (t != null) {
                    //getNavigator().failed(t);

                }
            }
        });
    }

    private void mDeleteUser(String token) {
        mProgressDialog = ProgressDialog.show(this, "Getting all users"
                , "Please Wait...", true);
        mService.syncUsers(token).enqueue(new Callback<GetAllUser>() {
            @Override
            public void onResponse(Call<GetAllUser> call, Response<GetAllUser> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    GetAllUser resp = response.body();
                    List<UserInfo> list = response.body().getData().getUserInfo();
                    userRecyclerView.setVisibility(View.VISIBLE);
                    mUserAdapter = new GetAllUserAdapter(list, new GetAllUserAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(UserInfo item) {
                            Log.i("jjj", String.valueOf(item.getId()));
                            byte[] bytes;
                            String userId = String.valueOf(item.getId());
                            String idOnDevice = "DELETE_" + item.getIdOnDevice();
                            Log.i("id", idOnDevice);
                            bytes = idOnDevice.getBytes();
                            connectedThread.write(bytes);

                        }
                    });
                    userRecyclerView.setAdapter(mUserAdapter);


                    Log.i("success", "onResponse: " + resp);

                } else {
                    Log.i("error", "onResponse: not successful");
                }

            }

            @Override
            public void onFailure(Call<GetAllUser> call, Throwable t) {
                // mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
                if (t != null) {
                    //getNavigator().failed(t);

                }
            }
        });
    }

    private void addUser(String firstName, String lastName, String id, String token) {
        AdduserReq.Request request = new AdduserReq.Request(firstName, lastName, id);
        mService.addUser(request, token).enqueue(new Callback<AddUserRes>() {

            @Override
            public void onResponse(Call<AddUserRes> call, Response<AddUserRes> response) {
                // mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    AddUserRes resp = response.body();
                    String mesage = response.body().getMessage();
                    Log.i("success", "onResponse: " + resp);
                    Log.i("success", mesage);

                } else {
                    Log.i("error", "onResponse: not successful");
                }

            }

            @Override
            public void onFailure(Call<AddUserRes> call, Throwable t) {
                // mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
                if (t != null) {
                    //getNavigator().failed(t);

                }
            }
        });
    }

    private void deleteUser(String token, String id) {
        mProgressDialog = ProgressDialog.show(this, "Deleting User"
                , "Please Wait...", true);
        mService.deleteUser(token, id).enqueue(new Callback<AddUserRes>() {

            @Override
            public void onResponse(Call<AddUserRes> call, Response<AddUserRes> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    AddUserRes resp = response.body();
                    String userToken = response.body().getMessage();
                    String mesage = response.body().getMessage();
                    Log.i("success", "onResponse: " + resp);
                    Log.i("success", userToken);

                } else {
                    Log.i("error", "onResponse: not successful");
                }

            }

            @Override
            public void onFailure(Call<AddUserRes> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
                if (t != null) {
                    //getNavigator().failed(t);

                }
            }
        });
    }


    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(Constants.CONNECTING_STATUS, 6, -1).sendToTarget();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(Constants.CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        byte[] buffer;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            Constants.MESSAGE_READ, incomingMessage);
                    readMsg.sendToTarget();
                    Log.d(TAG, "InputStream: " + readMsg.obj);
                    Log.d(TAG, "InputStream: " + readMsg.getClass());
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream." + e.getMessage());
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes);
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        Constants.MESSAGE_WRITE, -1, -1, buffer);
                writtenMsg.sendToTarget();
                Log.d(TAG, "write: Writing to outputstream: " + writtenMsg);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(Constants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null) {
            createConnectThread.cancel();
        }
        Intent a = new Intent(getApplicationContext(), SelectDeviceActivity.class);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null)
            if (mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
    }

}