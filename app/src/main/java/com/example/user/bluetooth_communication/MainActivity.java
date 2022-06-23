package com.example.user.bluetooth_communication;

import static com.example.user.bluetooth_communication.Constants.CONNECTING_STATUS;
import static com.example.user.bluetooth_communication.Constants.MESSAGE_READ;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDialog;

    private TextView mTextView;
    private Button openBtn;
    private Button deleteBtn;
    private Button addBtn;
    public static Handler handler;
    private LinearLayout linearLayout;
    public GetAllUserAdapter mUserAdapter;
    private RecyclerView userRecyclerView;
    EditText mLastname;
    TextView mText;

    String addressName;
    private Button sendOk;
    private EditText mFirstName;
    LinearLayout mRelativeLayout;
    String id;

    BluetoothConnectionService mBluetoothConnection;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothDevice mBTDevice;
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MiwiaWF0IjoxNjU1NDg1MzIyfQ.fUFKO585uH58hqpghn7w9vucuHAHw-O2IewwStXNxAg";

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public NewAdapter mNewAdapter;
    RecyclerView recyclerView;
    public ArrayList<UserInfo> userList = new ArrayList<>();
    public UserService mService = AppUtils.mService();
    TextView battery;
    String accessToken;
    SharedPref sharedPref;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = new SharedPref(getApplicationContext());
        accessToken = sharedPref.getUser();
        mProgressDialog = ProgressDialog.show(this, "Connecting"
                , "Please Wait...", true);
        Log.i("tok", accessToken);

        mBTDevices = new ArrayList<>();
        userList = new ArrayList<>();
        //btnPair = findViewById(R.id.btnFindUnpairedDevices);
        mTextView = findViewById(R.id.text_name);
        openBtn = findViewById(R.id.open);
        deleteBtn = findViewById(R.id.deleteUser);
        addBtn = findViewById(R.id.addUser);
        openBtn.setOnClickListener(this);
        userRecyclerView = findViewById(R.id.deleteList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mText = findViewById(R.id.text_change);
        battery = findViewById(R.id.batteryLife);
        toolbar = findViewById(R.id.toolbar);
        mBluetoothConnection = new BluetoothConnectionService(MainActivity.this, handler);

        linearLayout = findViewById(R.id.dataLayout);
        sendOk = findViewById(R.id.sndCommand);
        sendOk.setOnClickListener(this);
        mFirstName = findViewById(R.id.mFirstname);
        mLastname = findViewById(R.id.mLastName);
        mRelativeLayout = findViewById(R.id.nameLayout);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes;
                addBtn.setVisibility(View.GONE);
                mRelativeLayout.setVisibility(View.VISIBLE);
                String btnAdd = "SYNC_?";
                bytes = btnAdd.getBytes();
                mGetUser(accessToken);
                mBluetoothConnection.write(bytes);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBtn.setVisibility(View.GONE);
                mDeleteUser(token);
            }
        });


        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 6:
                                if (mBTDevice.getAddress() != null) {
                                    toolbar.setSubtitle("Connected to " + mBTDevice.getName());
                                    //mTextView.setText("bluetooth is connected to " + mBTDevice.getName());
//                                    String sendBattery = "BATTERY_?";
//                                    bytes = sendBattery.getBytes();
//                                    mBluetoothConnection.write(bytes);
//                                    Toast.makeText(getApplicationContext(), "battry sent to use" + sendBattery, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case -1:
                                toolbar.setSubtitle("Connected to " + mBTDevice.getName());
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
                                    Toast.makeText(getApplicationContext(), "Start process again", Toast.LENGTH_SHORT).show();
                                    break;
                                case "place_finger_again":
                                    Toast.makeText(getApplicationContext(), "Ask user to place same finger again", Toast.LENGTH_SHORT).show();
                                    break;
                                case "prints_saved_with_id":
                                    addUser(mFirstName.getText().toString(), mLastname.getText().toString(), parts[1], accessToken);
                                    Toast.makeText(getApplicationContext(), "User id Saved", Toast.LENGTH_SHORT).show();
                                    Log.i("jjj", parts[1]);
                                    break;
                                case "BATTERY":
                                    battery.setText(parts[1]);
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
                                        mBluetoothConnection.write(bytes);
                                    }
                                    Toast.makeText(getApplicationContext(), "User Sync" + id, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View view) {
        byte[] bytes;
        switch (view.getId()) {
            case R.id.sndCommand:
                String btnAdd = "ADD";
                bytes = btnAdd.getBytes();
                mBluetoothConnection.write(bytes);
                Toast.makeText(this, "add sent to user" + btnAdd, Toast.LENGTH_SHORT).show();
                break;
            case R.id.open:
                String btnOpen = openBtn.getText().toString().toUpperCase(Locale.ROOT);
                bytes = btnOpen.getBytes();
                mBluetoothConnection.write(bytes);
                Toast.makeText(this, "open sent to user" + btnOpen, Toast.LENGTH_SHORT).show();
                break;
        }

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
                    mBluetoothConnection = new BluetoothConnectionService(MainActivity.this, handler);
                    startConnection();
                    addressName = mDevice.getAddress();
                    linearLayout.setVisibility(View.VISIBLE);
                    mProgressDialog.dismiss();
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
                    id = response.body().getData().getIdDevice();
                    Log.i(TAG, id);

                    Log.i("success", "onResponse: " + resp);
                    //Log.i("success", user);

                } else {
                    Log.i("error", "onResponse: not successful");
                }

            }

            @Override
            public void onFailure(Call<GetAllUser> call, Throwable t) {
                mProgressDialog.dismiss();
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
                    mUserAdapter = new GetAllUserAdapter(list,
                            new GetAllUserAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(UserInfo item) {
                                    Log.i("jjj", String.valueOf(item.getId()));
                                    byte[] bytes;
                                    String userId = String.valueOf(item.getId());
                                    String idOnDevice = "DELETE_" + item.getIdOnDevice();
                                    Log.i("id",idOnDevice);
                                    bytes = idOnDevice.getBytes();
                                    mBluetoothConnection.write(bytes);
                                    mUserAdapter.removeItem(item);
                                    deleteUser(accessToken,item.getIdOnDevice());
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

}
