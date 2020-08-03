package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
        pref.edit().putString("teamName",teamName).putString("teamID",teamID).commit();
        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);
    }
    public String teamIDGenerator(){
        String teamID = "12345678";
        //这里要到firebase检查有没有这个房号

        return teamID;
    }
}
