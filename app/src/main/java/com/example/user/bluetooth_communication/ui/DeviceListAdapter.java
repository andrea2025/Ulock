package com.example.user.bluetooth_communication.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.R;


import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> deviceList;
    private OnItemClickListener mClickListener;


    public DeviceListAdapter(Context context, List<Object> deviceList, OnItemClickListener mClickListener) {
        this.context = context;
        this.deviceList = deviceList;
        this.mClickListener = mClickListener;

    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_info_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        final DeviceInfoModel deviceInfoModel = (DeviceInfoModel) deviceList.get(position);
        itemHolder.bind(deviceInfoModel, mClickListener);
        if (deviceList.size() == 0) {
            itemHolder.linearLayout.setVisibility(View.GONE);
            itemHolder.textNoDevice.setVisibility(View.VISIBLE);
        } else {
            itemHolder.linearLayout.setVisibility(View.VISIBLE);
            itemHolder.textNoDevice.setVisibility(View.GONE);
        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textAddress, textNoDevice;
        LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            textName = v.findViewById(R.id.textViewDeviceName);
            textAddress = v.findViewById(R.id.textViewDeviceAddress);
            linearLayout = v.findViewById(R.id.linearLayoutDeviceInfo);
            textNoDevice = v.findViewById(R.id.notFound);
        }

        public void bind(DeviceInfoModel deviceInfoModel, OnItemClickListener mClickListener) {
            textName.setText(deviceInfoModel.getDeviceName());
            textAddress.setText(deviceInfoModel.getDeviceHardwareAddress());
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(deviceInfoModel);
                }
            });

        }


//        public void bind(Object o, OnItemClickListener mClickListener) {
//            textName.setText();
//            textAddress.setText(deviceInfoModel.getDeviceHardwareAddress());
//
//            // When a device is selected
//            linearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mClickListener.onItemClick(o);
////                Intent intent = new Intent(context,MainActivity.class);
////                // Send device details to the MainActivity
////                intent.putExtra("deviceName", deviceInfoModel.getDeviceName());
////                intent.putExtra("deviceAddress",deviceInfoModel.getDeviceHardwareAddress());
////                // Call MainActivity
////                context.startActivity(intent);
//
//                }
//            });
//            if (deviceList.size() == 0){
//                linearLayout.setVisibility(View.GONE);
//                textNoDevice.setVisibility(View.VISIBLE);
//            }else {
//                linearLayout.setVisibility(View.VISIBLE);
//                textNoDevice.setVisibility(View.GONE);
//            }
//        }

    }
    @Override
    public int getItemCount() {
        int dataCount = deviceList.size();
        return dataCount;
    }


public interface OnItemClickListener {
    void onItemClick(DeviceInfoModel deviceInfoModel);
}
}
