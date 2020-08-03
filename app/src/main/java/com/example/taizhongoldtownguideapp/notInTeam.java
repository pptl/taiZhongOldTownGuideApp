package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class notInTeam extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_in_team);

        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        String userName = pref.getString("userName","error");

        textView = findViewById(R.id.notInTeam_textView);
        String wellcomeText = "歡迎你，" + userName;

        textView.setText(wellcomeText);

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
