package com.example.user.bluetooth_communication.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.R;
import com.example.user.bluetooth_communication.databinding.ActivityNewDeviceHomeBinding;
import com.example.user.bluetooth_communication.remote.AppUtils;
import com.example.user.bluetooth_communication.remote.Model.Request.AdduserReq;
import com.example.user.bluetooth_communication.remote.Model.Response.AddUserRes;
import com.example.user.bluetooth_communication.remote.Model.Response.GetAllUser;
import com.example.user.bluetooth_communication.remote.Model.Response.NextIdResponse;
import com.example.user.bluetooth_communication.remote.Model.Response.UserInfo;
import com.example.user.bluetooth_communication.remote.SharedPref;
import com.example.user.bluetooth_communication.remote.UserService;
import com.example.user.bluetooth_communication.ui.Utils.BluetoothConnectionService;
import com.example.user.bluetooth_communication.ui.Utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewDeviceHomeActivity extends AppCompatActivity {
    private static final String TAG = "NewDeviceHomeActivity";
    LayoutInflater layoutInflater;
    AlertDialog alert;
    View inflator;
    private ActivityNewDeviceHomeBinding binding;

    BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDialog;
    public static Handler handler;
    public GetAllUserAdapter getAllUserAdapter;
    private RecyclerView userRecyclerView;
    String mlastname;
    TextView mText;
    private String mFirstName;
    BluetoothConnectionService mBluetoothConnection;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public ArrayList<UserInfo> userList = new ArrayList<>();
    public UserService mService = AppUtils.mService();
    TextView battery;
    String accessToken;
    SharedPref sharedPref;
    EditText searchEdit;
    CardView mCardView;
    String nextId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewDeviceHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        sharedPref = new SharedPref(getApplicationContext());
        accessToken = sharedPref.getUser();
        mBTDevices = new ArrayList<>();
        userList = new ArrayList<>();
        //  mTextView = findViewById(R.id.text_name);
        userRecyclerView = findViewById(R.id.deleteList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setHasFixedSize(true);
       // mText = findViewById(R.id.textVisible);
        mBluetoothConnection = new BluetoothConnectionService(NewDeviceHomeActivity.this, handler);

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        searchEdit = findViewById(R.id.searchEdit);
        mCardView = findViewById(R.id.cardView);
        mText = findViewById(R.id.textVisible);

        binding.btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes;
                String btnOpen = binding.btnOpen.getText().toString().toUpperCase(Locale.ROOT);
                bytes = btnOpen.getBytes();
               mBluetoothConnection.write(bytes);
            }
        });


        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                Log.i(TAG, "onTextChanged: hhhh");
                getAllUserAdapter.getFilter().filter(charSequence);
                if (getAllUserAdapter.getItemCount() == 0) {
                    mText.setVisibility(View.VISIBLE);
                } else {
                    mText.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: kkkk");
            }
        });

        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 6:
                                if (mBTDevice.getAddress() != null) {
                                    binding.toolbar.setSubtitle("Connected to " + mBTDevice.getName());
                                    mCardView.setVisibility(View.VISIBLE);
                                    mDeleteUser(accessToken);
                                }
                                break;
                            case -1:
                                binding.toolbar.setSubtitle("Not Connected to " + mBTDevice.getName());
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
                                    Toast.makeText(getApplicationContext(), "Start process again", Toast.LENGTH_SHORT).show();
                                    break;
                                case "place_finger_again":
                                    Toast.makeText(getApplicationContext(), "Ask user to place same finger again", Toast.LENGTH_SHORT).show();
                                    break;
                                case "prints_saved_with_id":
                                    alert.dismiss();
                                    addUser(mFirstName, mlastname, parts[1], accessToken);
                                    Toast.makeText(getApplicationContext(), "User Saved Successfully", Toast.LENGTH_SHORT).show();
                                    mDeleteUser(accessToken);
                                    break;
                                case "BATTERY":
                                    battery.setText(parts[1]);
                                    Toast.makeText(getApplicationContext(), "Battery %", Toast.LENGTH_SHORT).show();
                                    break;
                                case "SYNC_YES":
                                    Log.i("user", arduinoMsg);
//                                    mGetUser(accessToken);
                                    break;
                                case "DELETE":
                                    getAllUserAdapter.removeItem(parts[1]);
                                    deleteUser(accessToken, parts[1]);
                            }
                        } catch (Exception e) {
                            Log.i("jjj", e.getLocalizedMessage());
                        }
                        break;

                }
            }
        };


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextId(accessToken);
//                byte[] bytes;
//                String btnAdd = "SYNC_?";
//                bytes = btnAdd.getBytes();
//                mBluetoothConnection.write(bytes);

                layoutInflater = LayoutInflater.from(NewDeviceHomeActivity.this);
                inflator = layoutInflater.inflate(R.layout.add_user_layout, null);
                alert = new AlertDialog.Builder(NewDeviceHomeActivity.this).create();

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
                            mlastname = lastName.getText().toString();
                            Log.i("hhhhh", mFirstName + mlastname);
                            byte[] mByte;
                            String btnAdd = "ADD_"+nextId;
                            mByte = btnAdd.getBytes();
                            mBluetoothConnection.write(mByte);
                        }
                    }
                });
                alert.show();
            }
        });
    }


    //create method for starting connection
    //***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection() {
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d("....", "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device, uuid);
    }

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                    Log.i("name", mDevice.getName());
                    mBluetoothConnection = new BluetoothConnectionService(NewDeviceHomeActivity.this, handler);
                    startConnection();
//                    addressName = mDevice.getAddress();
//                    linearLayout.setVisibility(View.VISIBLE);
                    // mProgressDialog.dismiss();
                }
                //case2: creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Intent a = new Intent(getApplicationContext(), SelectDeviceActivity.class);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
//        unregisterReceiver(mBroadcastReceiver1);
//        unregisterReceiver(mBroadcastReceiver2);
//        unregisterReceiver(mBroadcastReceiver3);
//        unregisterReceiver(mBroadcastReceiver4);
        // mBluetoothAdapter.cancelDiscovery();
        if (mProgressDialog != null)
            if (mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
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
                        mBluetoothConnection.write(bytes);
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
                    getAllUserAdapter = new GetAllUserAdapter(list,
                            new GetAllUserAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(UserInfo item) {
                                    Log.i("jjj", String.valueOf(item.getId()));
                                    byte[] bytes;
                                    String userId = String.valueOf(item.getId());
                                    String idOnDevice = "DELETE_" + item.getIdOnDevice();
                                    Log.i("id", idOnDevice);
                                    bytes = idOnDevice.getBytes();
                                    mBluetoothConnection.write(bytes);

                                }
                            });
                    userRecyclerView.setAdapter(getAllUserAdapter);
                    Log.i("success", "onResponse: " + resp);

                } else {
                    Log.i("error", "onResponse: not successful");
                }

            }

            @Override
            public void onFailure(Call<GetAllUser> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
                if (t != null) {
                    //

                }
            }
        });
    }

    private void addUser(String firstName, String lastName, String id, String token) {
        mProgressDialog = ProgressDialog.show(this, "Adding user"
                , "Please Wait...", true);
        AdduserReq.Request request = new AdduserReq.Request(firstName, lastName, id);
        mService.addUser(request, token).enqueue(new Callback<AddUserRes>() {
            @Override
            public void onResponse(Call<AddUserRes> call, Response<AddUserRes> response) {
                mProgressDialog.dismiss();
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
                mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
                if (t != null) {
                    //
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
                }
            }
        });
    }


    private void getNextId(String token) {
        mService.nextId(token).enqueue(new Callback<NextIdResponse>() {
            @Override
            public void onResponse(Call<NextIdResponse> call, Response<NextIdResponse> response) {
                // mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    NextIdResponse resp = response.body();
                    String id = response.body().getNextId().getNextId();
                    Log.i(TAG, id);
                    if (id.isEmpty()) {
                        Log.i(TAG, "no id found");
                    } else {
                        nextId = id;
                        Log.i("jjj",id);
                        // byte[] bytes;
//                        String sendIdOnDevice = id;
//                        bytes = sendIdOnDevice.getBytes();
//                        connectedThread.write(bytes);
                    }

                    Log.i("success", "onResponse: " + resp);
                    //Log.i("success", user);

                } else {
                    Log.i("error", "onResponse: not successful");
                }

            }

            @Override
            public void onFailure(Call<NextIdResponse> call, Throwable t) {
                //mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
                if (t != null) {
                    //getNavigator().failed(t);

                }
            }
        });
    }

}