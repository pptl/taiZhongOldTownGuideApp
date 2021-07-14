package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.usrProject.taizhongoldtownguideapp.R;

public class ChangeUserIcon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_icon);
    }

    public void returnIcon1(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedIcon", R.drawable.user_icon1);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void returnIcon2(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedIcon",R.drawable.user_icon2);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void returnIcon3(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedIcon", R.drawable.user_icon3);
        setResult(RESULT_OK, intent);
        finish();
    }
}
