package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class joinTeam extends AppCompatActivity {
    private EditText teamNOEditText;
    private String teamNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);
        teamNOEditText = (EditText)findViewById(R.id.joinTeam_editText);

    }

    public void quickJoin(View view) {
        Intent intent = new Intent(this,whereIsMyFriend.class);
        startActivity(intent);
    }
}
