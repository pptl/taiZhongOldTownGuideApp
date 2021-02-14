package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TeamEntry extends AppCompatActivity {

    private TextView welcomeSpeechTextView;
    private String userName;
    private String teamID;
    private String userID;
    private String userIconPath;
    private FirebaseDatabase mDatabase;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_entry);

        pref = getSharedPreferences("userData",MODE_PRIVATE);
        userName = pref.getString("userName","error");

        mDatabase = FirebaseDatabase.getInstance();

        welcomeSpeechTextView = findViewById(R.id.notInTeam_textView);
        String wellcomeText = "歡迎你，" + userName;
        welcomeSpeechTextView.setText(wellcomeText);
    }

    //如果使用者選擇創建團隊
    public void goCreateTeam(View view) {
        Map<String, Object> user = new HashMap<>();
        DatabaseReference teamRef = mDatabase.getReference("team");

        //這裡要check teamID有沒有相撞
        userIconPath = pref.getString("userIconPath","user_icon1");
        user.put("userIconPath", userIconPath);
        user.put("userName",userName);
        user.put("isLeader",true);

        teamID = teamIDGenerator();
        //這裡在檢查有沒有重複的teamID
        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while(snapshot.child(teamID).getValue() != null) {
                    if (snapshot.child(teamID).getValue() == null){
                        Log.d("seeIsTeamIDBang","IDnobang!");
                    }
                    else {
                        Log.d("seeIsTeamIDBang","IDbang!");
                        teamID = teamIDGenerator();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userID = teamRef.child(teamID).child("userData").push().getKey();

        teamRef.child(teamID).child("userData").child(userID).setValue(user);

        pref.edit().putString("userID",userID).putString("teamID",teamID).putBoolean("inTeam",true).commit();

        Intent intent = new Intent(this, TeamTracker.class);
        startActivity(intent);
        finish();
    }

    //如果使用者選擇參加團隊
    public void goJoinTeam(View view) {
        Intent intent = new Intent(this, JoinTeam.class);
        startActivity(intent);
    }

    public String teamIDGenerator(){

        double rand = Math.random();
        String teamID = Double.toString(rand);
        teamID = teamID.substring(2,8);
        //这里要到firebase检查有没有重複的房號

        return teamID;
    }
}
