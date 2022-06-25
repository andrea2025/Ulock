package com.example.user.bluetooth_communication.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.R;

import com.example.user.bluetooth_communication.remote.Model.Response.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GetAllUserAdapter extends RecyclerView.Adapter<GetAllUserAdapter.ViewHolder> {
    private List<UserInfo> mData;
    private OnItemClickListener listener;

    public GetAllUserAdapter(List<UserInfo> mData, OnItemClickListener listener) {
        this.mData = mData;
        this.listener = listener;
    }


    @NonNull
    @Override
    public GetAllUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_user_layout, viewGroup, false);
        return new GetAllUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mData.get(i), listener);
        // viewHolder.mName.setText(mData.get(i).getFirstName());
        //viewHolder.mAddress.setText(mData.get(i));

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

        public void bind(final UserInfo item, final OnItemClickListener listener) {
            mName.setText(item.getFirstName() +" "+ item.getLastName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    public void removeItem(String id){
        UserInfo[] arr = mData.toArray(new UserInfo[mData.size()]);
        Log.i("arr", String.valueOf(arr));
        for (int i = 0; i < mData.size(); i++) {
            Log.i("jjjj",arr[i].getIdOnDevice());
            if (arr[i].getIdOnDevice().equals(id)) {
                mData.remove(arr[i]);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mData.size());
            }
        }

    }

    public interface OnItemClickListener {

        void onItemClick(UserInfo item);
    }

}
