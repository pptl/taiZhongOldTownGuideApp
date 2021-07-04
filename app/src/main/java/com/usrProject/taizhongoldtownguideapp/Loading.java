package com.usrProject.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.usrProject.taizhongoldtownguideapp.activity.TeamTracker;

public class Loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //等10秒
        new CountDownTimer(10000 , 1000 ) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                finish();
                Intent intent = new Intent(getApplicationContext(), TeamTracker.class);
                startActivity(intent);
            }

        }.start();
    }
}
