package com.usr.taizhongoldtownguideapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.List;


public class PersonalInfoPopUpWinRecycleViewAdapter extends RecyclerView.Adapter<PersonalInfoPopUpWinRecycleViewAdapter.friendListRecycleViewHolder> {

    private List<String> friendList;
    private DatabaseReference teamMemberRef;
    private final LayoutInflater mInflater;
    private GoogleMap mMap;
    public Context context;

    class friendListRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView wordItemView;
        public ImageView userIcon;
        public ImageView isLeaderIcon;
        final PersonalInfoPopUpWinRecycleViewAdapter mAdapter;

        public friendListRecycleViewHolder(View itemView, PersonalInfoPopUpWinRecycleViewAdapter adapter) {
            super(itemView);

            wordItemView = itemView.findViewById(R.id.user_context);
            userIcon = itemView.findViewById(R.id.user_icon);
            isLeaderIcon = itemView.findViewById(R.id.isLeaderIcon);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            final int mPosition = getLayoutPosition();
            teamMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String currUserID = friendList.get(mPosition);
                    Double mCurrentUserLatitude = snapshot.child(currUserID).child("userLatitude").getValue(Double.class);
                    Double mCurrentUserLongitude = snapshot.child(currUserID).child("userLongitude").getValue(Double.class);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentUserLatitude, mCurrentUserLongitude),20f));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public PersonalInfoPopUpWinRecycleViewAdapter(Context context, List<String> friendList, DatabaseReference teamMemberRef, GoogleMap map) {
        mInflater = LayoutInflater.from(context);
        this.mMap = map;
        this.friendList = friendList;
        this.teamMemberRef = teamMemberRef;
        this.context = context;
    }

    @NonNull
    @Override
    public friendListRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.person_info_recycle_view_item, parent, false);
        return new friendListRecycleViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final friendListRecycleViewHolder holder, final int position) {
        teamMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currUserID = friendList.get(position);
                String mCurrentName = snapshot.child(currUserID).child("userName").getValue(String.class);
                String mCurrentUserIconPath = snapshot.child(currUserID).child("userIconPath").getValue(String.class);
                Boolean mCurrentUserIsLeader = snapshot.child(currUserID).child("isLeader").getValue(Boolean.class);
                holder.wordItemView.setText(mCurrentName);
                int imageResource = context.getResources().getIdentifier("@drawable/" + mCurrentUserIconPath, null, context.getPackageName());
                holder.userIcon.setImageResource(imageResource);
                if (mCurrentUserIsLeader == null || !mCurrentUserIsLeader){
                    holder.isLeaderIcon.setVisibility(View.GONE);
                } else {
                    int leaderIcon = context.getResources().getIdentifier("@drawable/crown", null, context.getPackageName());
                    holder.isLeaderIcon.setVisibility(View.VISIBLE);
                    holder.isLeaderIcon.setImageResource(leaderIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
