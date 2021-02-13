package com.example.taizhongoldtownguideapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateNewMarker extends AppCompatActivity {

    private TimePicker picker;
    private EditText editText;
    private Switch aSwitch;
    private String teamID;
    private Boolean setNotice;
    private Button button;
    private float longitude;
    private float latitude;
    final int PICK_IMAGE_REQUEST = 2;
    private ImageView markerIcon;
    private String markerPath;
    private SharedPreferences pref;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        mDatabase = FirebaseDatabase.getInstance();

        pref = getSharedPreferences("userData",MODE_PRIVATE);
        longitude = pref.getFloat("Longitude",0);
        latitude = pref.getFloat("Latitude",0);
        teamID = pref.getString("teamID","error");
        markerPath = "location_icon";

        markerIcon = findViewById(R.id.addIcon_iconView);
        editText = findViewById(R.id.addIcon_editText);
        aSwitch = findViewById(R.id.setNotice_switch);
        button = findViewById(R.id.addLocation_button);
        picker = (TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);
        picker.setEnabled(false);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    picker.setEnabled(true);
                }
                else{
                    picker.setEnabled(false);
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> newMark = new HashMap<>();

                Intent intent = new Intent();
                intent.putExtra("markContext", editText.getText().toString());
                setResult(RESULT_OK, intent);

                newMark.put("markContext",editText.getText().toString());
                newMark.put("markLatitude",latitude);
                newMark.put("markLongitude",longitude);
                newMark.put("setRemind",true);
                newMark.put("markSetTime",picker.getHour()+" "+picker.getMinute());
                newMark.put("markPath",markerPath);

                mDatabase.getReference().child("team").child(teamID).child("marker").push().setValue(newMark);
                finish();
            }
        });
    }

    public void changeIcon(View view) {
        Intent intent = new Intent(this, ChangeMarkerIcon.class);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            markerPath = data.getStringExtra("userPickedMarker");
            int imageResource = getResources().getIdentifier("@drawable/" + markerPath, null, getPackageName());
            markerIcon.setImageResource(imageResource);
        }
    }


}
