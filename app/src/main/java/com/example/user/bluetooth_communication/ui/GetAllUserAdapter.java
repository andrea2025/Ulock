package com.example.user.bluetooth_communication.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.bluetooth_communication.R;

import com.example.user.bluetooth_communication.remote.Model.Response.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GetAllUserAdapter extends RecyclerView.Adapter<GetAllUserAdapter.ViewHolder> implements Filterable {
    private List<UserInfo> mData;
    private OnItemClickListener listener;
    public List<UserInfo> filterList = new ArrayList<>();

    public GetAllUserAdapter(List<UserInfo> mData, OnItemClickListener listener) {
        this.mData = mData;
        this.listener = listener;
        this.filterList = mData;
    }


    @NonNull
    @Override
    public GetAllUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_user_layout, viewGroup, false);
        return new GetAllUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(filterList.get(i), listener);

    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                String charString = charSequence.toString();
                if (!charString.isEmpty()) {
                    ArrayList<UserInfo> filters = new ArrayList<>();
                    charString = charSequence.toString().toLowerCase().trim();
                    for (UserInfo memberItem : mData) {
                        if (memberItem.getFirstName().toLowerCase()
                                .contains(charSequence.toString().toLowerCase())) {
                            filters.add(memberItem);
                        }
                    }

                    results.count = filters.size();
                    results.values = filters;

                } else {
                    results.count = mData.size();
                    results.values = mData;
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (ArrayList<UserInfo>) filterResults.values;
                notifyDataSetChanged();


            }
        };

    }
//
//    private void filter(String text) {
//        //new array list that will hold the filtered data
//        ArrayList<UserInfo> filterdNames = new ArrayList<>();
//
//        //looping through existing elements
//        for (UserInfo memberItem : mData) {
//            //if the existing elements contains the search input
//            if (memberItem.getFirstName().toLowerCase().contains(text.toLowerCase())) {
//                //adding the element to filtered list
//                filterdNames.add(memberItem);
//            }
//        }
//
//        //calling a method of the adapter class and passing the
//        //filtered list;
//        adapter.filterList(filterdNames);
//    }
//
//
//    public void filterList(ArrayList<UserInfo> filterdNames) {
//        this.mData = filterdNames;
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        if (filterList.size() == 0) {
            return 0;
        } else {
            return filterList.size();
        }
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
