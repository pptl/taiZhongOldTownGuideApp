package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChangeMarkerIcon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_marker);
    }

    public void chooseMarker1(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedMarker", "bed_icon");
        setResult(RESULT_OK, intent);
        finish();
    }
    public void chooseMarker2(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedMarker", "food_icon");
        setResult(RESULT_OK, intent);
        finish();
    }
    public void chooseMarker3(View view) {
        Intent intent = new Intent();
        intent.putExtra("userPickedMarker", "location_icon");
        setResult(RESULT_OK, intent);
        finish();
    }

}
