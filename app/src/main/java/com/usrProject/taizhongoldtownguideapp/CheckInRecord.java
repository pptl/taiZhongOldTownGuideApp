package com.usrProject.taizhongoldtownguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CheckInRecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_record);

        TextView checkInConpletedTextView = findViewById(R.id.checkIn_record_completed_textView);
        TextView nextStopTitleTextView = findViewById(R.id.checkIn_record_next_stop_title);
        TextView nextStopContentTextView = findViewById(R.id.checkIn_record_next_stop_content);
        ProgressBar checkInCompletedProgressBar = findViewById(R.id.checkIn_record_progressBar);


        SharedPreferences pref = getSharedPreferences("userData", MODE_PRIVATE);

        nextStopTitleTextView.setText(pref.getString("nextStopTitle",""));
        nextStopContentTextView.setText(pref.getString("nextStopContent",""));
        checkInConpletedTextView.setText("已完成 " + pref.getInt("checkInCompleted", 0) + "/5" );
        //沒完成一個代表25%的進度
        checkInCompletedProgressBar.setProgress(pref.getInt("checkInCompleted",0)*25);






    }
}
