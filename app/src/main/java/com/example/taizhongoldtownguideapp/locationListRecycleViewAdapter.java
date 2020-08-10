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

public class locationListRecycleViewAdapter extends RecyclerView.Adapter<locationListRecycleViewAdapter.locationListRecycleViewHolder> {

    private List<String> locationList = new ArrayList<>();
    private final LayoutInflater mInflater;

    class locationListRecycleViewHolder extends RecyclerView.ViewHolder{
        public final TextView wordItemView;
        final locationListRecycleViewAdapter mAdapter;

        public locationListRecycleViewHolder(View itemView, locationListRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.location_context);
            this.mAdapter = adapter;
        }
    }

    public locationListRecycleViewAdapter(Context context, List<String> locationList) {
        mInflater = LayoutInflater.from(context);
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public locationListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.location_info_recycle_view_item,
                parent, false);
        return new locationListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull locationListRecycleViewHolder holder, int position) {
        String mCurrent = locationList.get(position);
        // Add the data to the view holder.
        holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
