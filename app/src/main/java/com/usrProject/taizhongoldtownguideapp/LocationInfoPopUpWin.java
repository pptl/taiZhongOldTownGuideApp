package com.usrProject.taizhongoldtownguideapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

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
    private Button createMarkerBtn;
    private Double mLatitude;
    private Double mLongitude;
    //private Context context;
    private Activity activity;
    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;


    public LocationInfoPopUpWin(final Context mContext, int xmlLayout, final GoogleMap map, Activity activity) {
        super(mContext, xmlLayout);
        this.activity = activity;

        mDatabase = FirebaseDatabase.getInstance();

        pref = mContext.getSharedPreferences("userData",mContext.MODE_PRIVATE);
        teamID = pref.getString("teamID","error");

        mLatitude = Double.longBitsToDouble(pref.getLong("mLatitude",0));
        mLongitude = Double.longBitsToDouble(pref.getLong("mLongitude",0));

        createMarkerBtn = getView().findViewById(R.id.create_marker_btn);
        //必須在getDeviceLocation()後面，因為會需要用到getDeviceLocation獲取的使用者位置mCurrentLocation

        createMarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation(mLatitude, mLongitude);
                //Log.d("sayHello","sayHello");
            }
        });

        mDatabase.getReference().child("team").child(teamID).child("marker");
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

    public void addLocation(double latitude, double longitude) {
        Intent intent = new Intent(this.activity, CreateNewMarker.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        this.activity.startActivityForResult(intent,ADD_LOCATION_ACTIVITY_REQUEST_CODE);


    }

}