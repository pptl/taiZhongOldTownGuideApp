package com.usrProject.taizhongoldtownguideapp.activity;

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
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamEntry extends AppCompatActivity {
    private TextView welcomeTitleTextView;
    private String userName;
    private String teamID;
    private String userID;
    private int userIconPath;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_entry);

        pref = getSharedPreferences(UserSchema.SharedPreferences.USER_DATA, MODE_PRIVATE);
        userName = pref.getString("userName","None");
        welcomeTitleTextView = findViewById(R.id.notInTeam_textView);
        String wellcomeText = "歡迎你，" + userName;
        welcomeTitleTextView.setText(wellcomeText);
    }

    //如果使用者選擇創建團隊
    public void goCreateTeam(View view) {
        Map<String, Object> user = new HashMap<>();
        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference("team");

        //這裡要check teamID有沒有相撞
        userIconPath = pref.getInt("userIconPath",R.drawable.user_icon1);
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
//      跟database取的唯一直
        userID = teamRef.child(teamID).child("userData").push().getKey();

        teamRef.child(teamID).child("userData").child(userID).setValue(user);

        pref.edit().putString("userID",userID).putString("teamID",teamID).putBoolean("inTeam",true).commit();
        pref.edit().putString("roomType","multiUsers").commit();
        Intent intent = new Intent(this, TeamTracker.class);
        startActivity(intent);
        finish();
    }

    //如果使用者選擇參加團隊
    public void goJoinTeam(View view) {
        Intent intent = new Intent(this, JoinTeam.class);
        startActivity(intent);
    }



    public void goSelf(View view) {
        Map<String, Object> user = new HashMap<>();
        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference("team");

        //這裡要check teamID有沒有相撞
        userIconPath = pref.getInt("userIconPath",R.drawable.user_icon1);
        user.put("userIconPath", userIconPath);
        user.put("userName",userName);
        user.put("isLeader",true);

        teamID = teamIDGenerator();
        //這裡在檢查有沒有重複的teamID
        ValueEventListener listner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while (snapshot.child(teamID).getValue() != null) {
                    if (snapshot.child(teamID).getValue() == null) {
                    } else {
                        teamID = teamIDGenerator();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        teamRef.addListenerForSingleValueEvent(listner);

        userID = teamRef.child(teamID).child("userData").push().getKey();

        teamRef.child(teamID).child("userData").child(userID).setValue(user);

        teamRef.removeEventListener(listner);
        pref.edit().putString("userID", userID).putString("teamID",teamID).putBoolean("inTeam",true).commit();
        pref.edit().putString("roomType","singleUser").commit();

        Intent intent = new Intent(this, TeamTracker.class);
        startActivity(intent);
        finish();
    }

    public String teamIDGenerator(){
        String uuid = StringUtils.substring(UUID.randomUUID().toString(),0,8);
        return uuid;
    }
}
