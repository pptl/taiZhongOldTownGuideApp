package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class LocationInfoPopUpWin extends CustomPopUpWin {

    private List<String> locationList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LocationInfoPopUpWinRecycleViewAdapter mAdapter;
    private String teamID;
    private SharedPreferences pref;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamMarkerRef;

    public LocationInfoPopUpWin(final Context mContext, int xmlLayout, final GoogleMap map) {
        super(mContext, xmlLayout);

        mDatabase = FirebaseDatabase.getInstance();

        pref = mContext.getSharedPreferences("userData",mContext.MODE_PRIVATE);
        teamID = pref.getString("teamID","error");

        if(mDatabase.getReference().child("team").child(teamID).child("marker") != null) {
            teamMarkerRef = mDatabase.getReference().child("team").child(teamID).child("marker");
            mRecyclerView = getView().findViewById(R.id.showLocation_recyclerView);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            teamMarkerRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    locationList.add(snapshot.getKey());
                    mAdapter = new LocationInfoPopUpWinRecycleViewAdapter(mContext,locationList,teamMarkerRef,map);
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

}