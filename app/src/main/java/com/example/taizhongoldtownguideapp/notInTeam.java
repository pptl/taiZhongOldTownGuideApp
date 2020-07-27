package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class notInTeam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_in_team);
    }



    public void goCreateTeam(View view) {
        Intent intent = new Intent(this,createTeam.class);
        startActivity(intent);
    }
    public void goJoinTeam(View view) {
        Intent intent = new Intent(this,joinTeam.class);
        startActivity(intent);

    }
}
