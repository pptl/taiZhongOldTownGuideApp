package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class friendListRecycleViewAdapter extends RecyclerView.Adapter<friendListRecycleViewAdapter.friendListRecycleViewHolder> {

    private List<String> friendList = new ArrayList<>();
    private final LayoutInflater mInflater;

    class friendListRecycleViewHolder extends RecyclerView.ViewHolder{
        public final TextView wordItemView;
        final friendListRecycleViewAdapter mAdapter;

        public friendListRecycleViewHolder(View itemView, friendListRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.friendName);
            this.mAdapter = adapter;
        }
    }

    public friendListRecycleViewAdapter(Context context, List<String> friendList) {
        mInflater = LayoutInflater.from(context);
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public friendListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.person_info_recycle_view_item,
                parent, false);
        return new friendListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull friendListRecycleViewHolder holder, int position) {
        String mCurrent = friendList.get(position);
        // Add the data to the view holder.
        holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
