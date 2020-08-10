package com.example.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

public class addLocation extends AppCompatActivity {

    private TimePicker picker;
    private EditText editText;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        /*
        picker = (TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        editText.findViewById(R.id.addIcon_editText);
    */

    }

    public void changeIcon(View view) {

    }

    public void addMark(View view) {
        //这里要把资料传到firebase

    }
}
