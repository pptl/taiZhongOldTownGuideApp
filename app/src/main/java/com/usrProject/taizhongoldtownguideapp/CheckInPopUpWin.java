package com.usrProject.taizhongoldtownguideapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class CheckInPopUpWin extends CustomPopUpWin{

    public CheckInPopUpWin(Context mContext, int xmlLayout, int completedNum, String title) {
        super(mContext, xmlLayout, false);
        Button closeWinBtn = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_close_btn);
        Button completedBtn = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_button);
        TextView completedCountTextView = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_textView);
        TextView titleTextView = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_title_textView);
        ProgressBar progressBar = this.getView().findViewById(R.id.check_in_record_pop_up_win_progressBar);

        completedCountTextView.setText(completedNum+"/5");
        titleTextView.setText(title);
        progressBar.setProgress(completedNum*25);

        closeWinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }
}
