package com.example.user.bluetooth_communication.ui;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.R;


import java.util.ArrayList;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> mData;
    private LayoutInflater mInflater;
    private OnItemClickListener mClickListener;

    public NewAdapter(ArrayList<BluetoothDevice> mData, OnItemClickListener mClickListener) {
        this.mData = mData;
        // this.mInflater = LayoutInflater.from(context);
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public NewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_adapter_view, viewGroup, false);
        return new NewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(mData.get(i), mClickListener);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mName = (TextView) itemView.findViewById(R.id.tvDeviceName);
            this.mAddress = (TextView) itemView.findViewById(R.id.tvDeviceAddress);

        }

        public void bind(final BluetoothDevice item, OnItemClickListener listener) {
            mName.setText(item.getName());
            mAddress.setText(item.getAddress());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice item);
    }


}
