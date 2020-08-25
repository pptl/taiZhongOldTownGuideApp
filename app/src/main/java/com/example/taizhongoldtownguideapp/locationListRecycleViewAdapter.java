package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class locationListRecycleViewAdapter extends RecyclerView.Adapter<locationListRecycleViewAdapter.locationListRecycleViewHolder> {

    private List<String> locationList = new ArrayList<>();
    private CollectionReference markRef;
    private final LayoutInflater mInflater;
    public Context context;

    class locationListRecycleViewHolder extends RecyclerView.ViewHolder{
        public final TextView wordItemView;
        public ImageView markerIcon;
        final locationListRecycleViewAdapter mAdapter;

        public locationListRecycleViewHolder(View itemView, locationListRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.location_context);
            markerIcon = itemView.findViewById(R.id.location_icon);
            this.mAdapter = adapter;
        }
    }

    public locationListRecycleViewAdapter(Context context, List<String> locationList,CollectionReference markRef) {
        mInflater = LayoutInflater.from(context);
        this.markRef = markRef;
        this.locationList = locationList;
        this.context = context;
    }

    @NonNull
    @Override
    public locationListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.location_info_recycle_view_item,
                parent, false);
        return new locationListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final locationListRecycleViewHolder holder, final int position) {
        markRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String mCurrentName = task.getResult().getDocuments().get(position).get("markContext").toString();
                    String mCurrentMarkerIconPath = task.getResult().getDocuments().get(position).get("markPath").toString();
                    holder.wordItemView.setText(mCurrentName);

                    int imageResource = context.getResources().getIdentifier("@drawable/" + mCurrentMarkerIconPath, null, context.getPackageName());
                    holder.markerIcon.setImageResource(imageResource);
                } else {
                    Log.d("firebaseMember", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
