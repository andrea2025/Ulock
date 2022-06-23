package com.example.user.bluetooth_communication;

import static android.content.ContentValues.TAG;
import static com.example.user.bluetooth_communication.Constants.CONNECTING_STATUS;
import static com.example.user.bluetooth_communication.Constants.MESSAGE_READ;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.remote.AppUtils;
import com.example.user.bluetooth_communication.remote.Model.Request.AdduserReq;
import com.example.user.bluetooth_communication.remote.Model.Response.AddUserRes;
import com.example.user.bluetooth_communication.remote.Model.Response.GetAllUser;
import com.example.user.bluetooth_communication.remote.Model.Response.UserInfo;
import com.example.user.bluetooth_communication.remote.SharedPref;
import com.example.user.bluetooth_communication.remote.UserService;

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

public class PairedActivity extends AppCompatActivity implements View.OnClickListener {
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
    private Button openBtn;
    private Button deleteBtn;
    private Button addBtn;
    private LinearLayout linearLayout;
    EditText mLastname;
    TextView mText;
    ProgressDialog mProgressDialog;
    String addressName;
    private Button sendOk;
    private EditText mFirstName;
    LinearLayout mRelativeLayout;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        accessToken = SharedPref.getString(Constants.TOKEN, "");
        Log.i("tok", accessToken);
        userList = new ArrayList<>();
        userRecyclerView = findViewById(R.id.deleteLists);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        linearLayout = findViewById(R.id.pairedataLayout);
        mRelativeLayout = findViewById(R.id.nameLayout);
        sendOk = findViewById(R.id.sndCommand);
        sendOk.setOnClickListener(this);
        openBtn = findViewById(R.id.open);
        deleteBtn = findViewById(R.id.deleteUser);
        addBtn = findViewById(R.id.addUser);
        mLastname = findViewById(R.id.mLastName);
        mFirstName = findViewById(R.id.mFirstname);
        openBtn.setOnClickListener(this);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes;
                addBtn.setVisibility(View.GONE);
                mRelativeLayout.setVisibility(View.VISIBLE);
                String btnAdd = "SYNC_?";
                bytes = btnAdd.getBytes();
                connectedThread.write(bytes);
                mGetUser(accessToken);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBtn.setVisibility(View.GONE);
                mDeleteUser(accessToken);
            }
        });


        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null) {
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progree and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            //progressBar.setVisibility(View.VISIBLE);
            //  buttonConnect.setEnabled(false);

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
                    case CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 6:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                linearLayout.setVisibility(View.VISIBLE);
//                                progressBar.setVisibility(View.GONE);
//                                buttonConnect.setEnabled(true);
//                                buttonToggle.setEnabled(true);
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
//                                progressBar.setVisibility(View.GONE);
//                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
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
                                    Toast.makeText(getApplicationContext(), "start process again", Toast.LENGTH_SHORT).show();
                                    break;
                                case "place_finger_again":
                                    Toast.makeText(getApplicationContext(), "Ask user to place same finger again", Toast.LENGTH_SHORT).show();
                                    break;
                                case "prints_saved_with_id":
                                    addUser(mFirstName.getText().toString(), mLastname.getText().toString(), parts[1], accessToken);
                                    Toast.makeText(getApplicationContext(), "FingerPrint saved with id", Toast.LENGTH_SHORT).show();
                                    break;
                                case "BATTERY":
                                    Toast.makeText(getApplicationContext(), "Battery %", Toast.LENGTH_SHORT).show();
                                    break;
                                case "SYNC_YES":
                                    Log.i("user", arduinoMsg);
                                    if (id.length() == 0) {
                                        Log.i(TAG, "no id found");
                                    } else {
                                        byte[] bytes;
                                        String sendIdOnDevice = id;
                                        bytes = sendIdOnDevice.getBytes();
                                        connectedThread.write(bytes);
                                    }
                                    Toast.makeText(getApplicationContext(), "User sync" + id, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (Exception e) {
                            Log.i("jjj", e.getLocalizedMessage());
                        }
                        break;
                }
            }
        };

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
                            Log.i("id",idOnDevice);
                            bytes = idOnDevice.getBytes();
                            connectedThread.write(bytes);
                            mUserAdapter.removeItem(item);
                            deleteUser(accessToken, item.getIdOnDevice());

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

    @Override
    public void onClick(View view) {
        byte[] bytes;
        switch (view.getId()) {
            case R.id.sndCommand:
                String btnAdd = "ADD";
                bytes = btnAdd.getBytes();
                connectedThread.write(bytes);
                Toast.makeText(this, "add sent to user" + btnAdd, Toast.LENGTH_SHORT).show();
                break;
            case R.id.open:
                String btnOpen = openBtn.getText().toString().toUpperCase(Locale.ROOT);
                bytes = btnOpen.getBytes();
                connectedThread.write(bytes);
                Toast.makeText(this, "open sent to user" + btnOpen, Toast.LENGTH_SHORT).show();
                break;
        }

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
                handler.obtainMessage(CONNECTING_STATUS, 6, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
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
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
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