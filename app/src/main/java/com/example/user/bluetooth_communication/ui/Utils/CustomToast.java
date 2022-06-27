package com.example.user.bluetooth_communication.ui.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.bluetooth_communication.R;

public class CustomToast {

    public static void showToast(Context context, String msg, int duration) {


       Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
        toast.show();
////        View view = toast.getView();
////        view.setBackgroundResource(android.R.drawable.toast_frame);
////        view.setBackgroundColor(context.getResources().getColor(R.color.blue_shade));
//        toast.setGravity(Gravity.TOP, 0, 0);
//       // TextView text = view.findViewById(android.R.id.message);
////        text.setBackground(context.getResources().getDrawable(R.drawable.custom_toast));
//        //text.setTextColor(context.getResources().getColor(R.color.white));
//        toast.show();
    }
}
