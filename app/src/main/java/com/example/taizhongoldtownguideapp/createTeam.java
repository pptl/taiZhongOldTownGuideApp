package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class createTeam extends AppCompatActivity {

    private EditText editText;
    private String teamName;
    private String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        editText = findViewById(R.id.createTeam_editText);

    }

    public void quickCreate(View view) {
        teamName = editText.getText().toString();
        //这里要备份到firebase去
        //这里需要一个乱数产生器
        teamID = teamIDGenerator();
        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        pref.edit().putString("teamName",teamName).putString("teamID",teamID).putBoolean("isLeader",true).commit();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teamID").add(teamID);

        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);
    }
    public String teamIDGenerator(){

        double rand = Math.random();
        String teamID = Double.toString(rand);
        teamID = teamID.substring(2,8);
        //这里要到firebase检查有没有这个房号ifno=>

        return teamID;
    }
}
