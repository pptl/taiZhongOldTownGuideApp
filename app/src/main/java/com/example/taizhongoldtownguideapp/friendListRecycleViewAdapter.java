package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import java.util.List;


public class friendListRecycleViewAdapter extends RecyclerView.Adapter<friendListRecycleViewAdapter.friendListRecycleViewHolder> {

    private List<String> friendList = new ArrayList<>();
    private CollectionReference teamMemberRef;
    private final LayoutInflater mInflater;
    private GoogleMap mMap;
    public Context context;


    class friendListRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView wordItemView;
        public ImageView userIcon;
        final friendListRecycleViewAdapter mAdapter;


        public friendListRecycleViewHolder(View itemView, friendListRecycleViewAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.user_context);
            userIcon = itemView.findViewById(R.id.user_icon);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            final int mPosition = getLayoutPosition();
            teamMemberRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Double mCurrentUserLatitude = (Double)task.getResult().getDocuments().get(mPosition).get("userLatitude");
                        Double mCurrentUserLongitude = (Double)task.getResult().getDocuments().get(mPosition).get("userLongitude");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentUserLatitude, mCurrentUserLongitude),20f));
                    } else {
                        Log.d("getuserLocation", "Error getting documents: ", task.getException());
                    }
                }
            });




        }
    }

    public friendListRecycleViewAdapter(Context context, List<String> friendList, CollectionReference teamMemberRef, GoogleMap map) {
        mInflater = LayoutInflater.from(context);
        this.mMap = map;
        this.friendList = friendList;
        this.teamMemberRef = teamMemberRef;
        this.context = context;
    }

    @NonNull
    @Override
    public friendListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.person_info_recycle_view_item,
                parent, false);
        return new friendListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final friendListRecycleViewHolder holder, final int position) {
        teamMemberRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String mCurrentName = task.getResult().getDocuments().get(position).get("userName").toString();
                            String mCurrentUserIconPath = task.getResult().getDocuments().get(position).get("userIconPath").toString();
                            holder.wordItemView.setText(mCurrentName);
                            int imageResource = context.getResources().getIdentifier("@drawable/" + mCurrentUserIconPath, null, context.getPackageName());
                            holder.userIcon.setImageResource(imageResource);
                        } else {
                            Log.d("firebaseMember", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
