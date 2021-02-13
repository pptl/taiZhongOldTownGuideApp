package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsRecycleViewAdapter extends RecyclerView.Adapter<NewsRecycleViewAdapter.postHolder> {

    private final List<String> titleList;
    private final List<String> urlList;
    private final LayoutInflater mInflater;
    private final Context context;

    class postHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView wordItemView;
        final NewsRecycleViewAdapter mAdapter;


        public postHolder(View itemView, NewsRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.post_title);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            Intent intent = new Intent(context, NewsInfo.class);
            intent.putExtra("title",titleList.get(mPosition));
            intent.putExtra("url",urlList.get(mPosition));
            context.startActivity(intent);
            //mWordList.set(mPosition, "Clicked! " + element);
            // Notify the adapter, that the data has changed so it can
            // update the RecyclerView to display the data.
            //mAdapter.notifyDataSetChanged();
        }
    }

    public NewsRecycleViewAdapter(Context context, List<String> titleList, List<String> urlList) {
        mInflater = LayoutInflater.from(context);
        this.titleList = titleList;
        this.urlList = urlList;
        this.context = context;
    }

    @Override
    public NewsRecycleViewAdapter.postHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(R.layout.post_recycle_view_item, parent, false);
        return new postHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(NewsRecycleViewAdapter.postHolder holder, int position) {
        // Retrieve the data for that position.
        String mCurrent = titleList.get(position);
        // Add the data to the view holder.
        if(position%2 == 1){
            holder.itemView.setBackgroundColor(Color.parseColor("#A6A6A6"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }
}
