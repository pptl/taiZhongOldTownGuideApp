package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class joinTeam extends AppCompatActivity {
    private EditText editText;
    private String teamName;
    private String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        editText = (EditText)findViewById(R.id.joinTeam_editText);

    }

    public void quickJoin(View view) {
        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);

        teamID = editText.getText().toString();
        //这里要去firebase用房间ID找房间，if有把用户加入,还要找出房间名字；
        teamName = "假的团队名";

        pref.edit().putString("teamName",teamName).putString("teamID",teamID).commit();

        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);
    }
}
