package com.usrProject.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class JoinTeam extends AppCompatActivity {
    private EditText editText;
    private String teamID;
    private String userName;
    private String userID;
    private String userIconPath;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamRef;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        editText = (EditText)findViewById(R.id.joinTeam_editText);
        pref = getSharedPreferences("userData",MODE_PRIVATE);

        userName = pref.getString("userName","error");
        userIconPath = pref.getString("userIconPath","user_icon1");

        mDatabase = FirebaseDatabase.getInstance();
        teamRef = mDatabase.getReference("team");
    }

    public void quickJoin(View view) {

        //這裡離要檢查輸入碼對不對
        teamID = editText.getText().toString();

        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(teamID).getValue() != null){
                    Map<String, Object> user = new HashMap<>();
                    user.put("userName",userName);
                    user.put("isLeader",false);
                    user.put("userLatitude",0.00);
                    user.put("userLongitude",0.00);
                    user.put("userIconPath", userIconPath);

                    userID = teamRef.child("userData").push().getKey();
                    teamRef.child(teamID).child("userData").child(userID).setValue(user);

                    pref.edit().putString("userName",userName).putString("userID",userID).putString("teamID",teamID).putBoolean("inTeam",true).putBoolean("isLeader",false).putFloat("userLatitude",0).putFloat("userLongitude",0).putString("userIconPath", userIconPath).commit();

                    teamRef.removeEventListener(this);
                    Intent intent = new Intent(getApplicationContext(), TeamTracker.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"房號不正確",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
