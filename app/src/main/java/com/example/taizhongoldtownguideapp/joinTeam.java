package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class joinTeam extends AppCompatActivity {
    private EditText editText;
    private String teamID;
    private String userName;
    private String userID;
    private String userIconPath;
    private FirebaseDatabase mDatabase;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        editText = (EditText)findViewById(R.id.joinTeam_editText);
        pref = getSharedPreferences("userData",MODE_PRIVATE);

        userName = pref.getString("userName","error");
        userIconPath = pref.getString("userIconPath","error");

        mDatabase = FirebaseDatabase.getInstance();


    }

    public void quickJoin(View view) {
        Map<String, Object> user = new HashMap<>();
        //這裡離要檢查輸入碼對不對
        teamID = editText.getText().toString();

        DatabaseReference teamRef = mDatabase.getReference("team").child(teamID);

        user.put("userName",userName);
        user.put("isLeader",false);
        user.put("userLatitude",0.00);
        user.put("userLongitude",0.00);
        user.put("userIconPath", userIconPath);

        userID = teamRef.child("userData").push().getKey();
        teamRef.child("userData").child(userID).setValue(user);

        pref.edit().putString("userName",userName).putString("userID",userID).putString("teamID",teamID).putBoolean("isLeader",false).putFloat("userLatitude",0).putFloat("userLongitude",0).putString("userIconPath", userIconPath).commit();

        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);
    }
}
