package com.usrProject.taizhongoldtownguideapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTaskProcess;
import com.usrProject.taizhongoldtownguideapp.schema.MarkTask;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

import static android.content.Context.MODE_PRIVATE;


public class CheckInPopUpWin extends CustomPopUpWin{
    CurrentTaskProcess currentTaskProcess;
    CheckInMarkerObject currentMarker;

    Button closeWinButton;
    Button cancelButtton;
    Button compeleteButton;
    TextView completedCountTextView;
    TextView titleTextView;
    ProgressBar progressBar;

    public CheckInPopUpWin(Context mContext, int xmlLayout, int completedNum, String title) {
        super(mContext, xmlLayout, false);
        closeWinButton = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_close_btn);
        cancelButtton = this.getView().findViewById(R.id.check_in_record_pop_up_win_cancel_button);
        compeleteButton = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_button);
        completedCountTextView = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_textView);
        titleTextView = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_title_textView);
        progressBar = this.getView().findViewById(R.id.check_in_record_pop_up_win_progressBar);

        final SharedPreferences pref = mContext.getSharedPreferences(UserSchema.USER_DATA, MODE_PRIVATE);
        Gson gson = new Gson();
        currentTaskProcess = gson.fromJson(pref.getString(MarkTask.CURRENT_TASK.key, null), CurrentTaskProcess.class);
        if(currentTaskProcess.tasksContent.isEmpty()){
            titleTextView.setText("此任務無打卡進度");
            completedCountTextView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            compeleteButton.setText("完成");
        }else{
            currentMarker = currentTaskProcess.tasksContent.get(currentTaskProcess.currentTask);
            titleTextView.setText(currentMarker.markTitle);
            completedCountTextView.setText(String.format("%d/%d",currentTaskProcess.currentTask,currentTaskProcess.tasksContent.size()));
            Double doneProcess = Double.valueOf(currentTaskProcess.currentTask) / Double.valueOf(currentTaskProcess.tasksContent.size());
            progressBar.setProgress((int) (doneProcess * 100.0));
        }


        closeWinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cancelButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        compeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StringUtils.equals(compeleteButton.getText().toString(), "完成")){
                    dismiss();
                    pref.edit().remove(MarkTask.CURRENT_TASK.key).commit();
                    return;
                }currentMarker.setChecked(true);
                currentTaskProcess.currentTask++;

                if(currentTaskProcess.currentTask < currentTaskProcess.tasksContent.size()){
                    currentMarker = currentTaskProcess.tasksContent.get(currentTaskProcess.currentTask);
                    titleTextView.setText(currentMarker.markTitle);
                    completedCountTextView.setText(String.format("%d/%d",currentTaskProcess.currentTask,currentTaskProcess.tasksContent.size()));
                    Double doneProcess = Double.valueOf(currentTaskProcess.currentTask) / Double.valueOf(currentTaskProcess.tasksContent.size());
                    progressBar.setProgress((int) (doneProcess * 100.0));
                    pref.edit().putString(MarkTask.CURRENT_TASK.key, new Gson().toJson(currentTaskProcess)).commit();
                }else{
                    titleTextView.setText("完成所有打卡任務");
                    completedCountTextView.setText(String.format("%d/%d",currentTaskProcess.currentTask,currentTaskProcess.tasksContent.size()));
                    Double doneProcess = Double.valueOf(currentTaskProcess.currentTask) / Double.valueOf(currentTaskProcess.tasksContent.size());
                    progressBar.setProgress((int) (doneProcess * 100.0));
                    compeleteButton.setText("完成");
                }
            }
        });


    }
}
