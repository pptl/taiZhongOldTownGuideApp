package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class createTeam extends AppCompatActivity {

    private EditText teamNameEditText;
    private String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        teamNameEditText = findViewById(R.id.createTeam_editText);
        teamName = teamNameEditText.getTransitionName();


    }

    public void quickCreate(View view) {
        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);
    }
}
