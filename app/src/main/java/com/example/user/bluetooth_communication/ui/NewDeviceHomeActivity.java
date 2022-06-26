package com.example.user.bluetooth_communication.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.R;
import com.example.user.bluetooth_communication.databinding.ActivityNewDeviceHomeBinding;
import com.example.user.bluetooth_communication.remote.AppUtils;
import com.example.user.bluetooth_communication.remote.Model.Response.UserInfo;
import com.example.user.bluetooth_communication.remote.SharedPref;
import com.example.user.bluetooth_communication.remote.UserService;
import com.example.user.bluetooth_communication.ui.Utils.BluetoothConnectionService;

import java.util.ArrayList;
import java.util.UUID;


public class NewDeviceHomeActivity extends AppCompatActivity {
    LayoutInflater layoutInflater;
    AlertDialog.Builder alert;
    View inflator;
    private ActivityNewDeviceHomeBinding binding;

    BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDialog;
    public static Handler handler;
    public GetAllUserAdapter mUserAdapter;
    private RecyclerView userRecyclerView;
    String mLastname;
    TextView mText;
    private String mFirstName;

    BluetoothConnectionService mBluetoothConnection;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothDevice mBTDevice;

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

        binding = ActivityNewDeviceHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutInflater = LayoutInflater.from(getApplicationContext());
                inflator = layoutInflater.inflate(R.layout.add_user_layout, null);
                alert = new AlertDialog.Builder(getApplicationContext());

                alert.setTitle("Add User");
                //alert.setMessage("Add A New User");
                alert.setView(inflator);
                alert.setCancelable(false);

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
                            // connectedThread.write(bytes);
                        }
                    }
                });

//                alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton)
//                    {
//                        String s1=et1.getText().toString();
//                        String s2=et2.getText().toString();
//                        //do operations using s1 and s2 here...
//                    }
//                });
//
//                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.cancel();
//                    }
//                });

                alert.show();
            }
        });
    }

}