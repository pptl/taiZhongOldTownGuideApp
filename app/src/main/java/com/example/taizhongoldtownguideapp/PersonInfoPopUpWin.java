package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;
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

public class PersonInfoPopUpWin extends CustomPopUpWin {

    private List<String> friendList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PersonalInfoPopUpWinRecycleViewAdapter mAdapter;
    private String teamID;
    private String teamName;//之後會擴充
    private TextView inviteCodeTextView;
    private String inviteCode;
    private SharedPreferences pref;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamMemberRef;

    public PersonInfoPopUpWin(final Context mContext, int xmlLayout, final GoogleMap map) {
        super(mContext, xmlLayout);

        mDatabase = FirebaseDatabase.getInstance();

        pref = mContext.getSharedPreferences("userData",mContext.MODE_PRIVATE);
        teamID = pref.getString("teamID","error");
        teamName = pref.getString("teamName","error");

        teamMemberRef = mDatabase.getReference().child("team").child(teamID).child("userData");

        inviteCodeTextView = getView().findViewById(R.id.personInfo_inviteCode_TextView);
        inviteCode = "團隊號碼："+ teamID;
        inviteCodeTextView.setText(inviteCode);

        teamMemberRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                friendList.add(snapshot.getKey());
                mAdapter = new PersonalInfoPopUpWinRecycleViewAdapter(mContext, friendList, teamMemberRef, map);
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

        mRecyclerView = getView().findViewById(R.id.showFriend_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }
}