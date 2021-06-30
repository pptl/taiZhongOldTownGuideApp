package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.usrProject.taizhongoldtownguideapp.R;
import com.usrProject.taizhongoldtownguideapp.TeamTracker;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckTasks;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CurrentTask;
import com.usrProject.taizhongoldtownguideapp.schema.MarkTask;
import com.usrProject.taizhongoldtownguideapp.schema.TaskSchema;
import com.usrProject.taizhongoldtownguideapp.schema.UserSchema;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskInfoActivity extends AppCompatActivity {
    private CheckTasks tasksInfo;
    private CurrentTask currentTask;
    private TextView taskTitleView;
    private TextView taskDescView;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        pref = getSharedPreferences(UserSchema.USER_DATA, MODE_PRIVATE);
//        init
        taskTitleView = findViewById(R.id.taskTitleView);
        taskDescView = findViewById(R.id.taskDescView);

        Intent intent = this.getIntent();
        tasksInfo = (CheckTasks) intent.getSerializableExtra(TaskSchema.TASK_INFO);

        taskTitleView.setText(tasksInfo.taskTitle);
        taskDescView.setText(tasksInfo.taskDesc);
    }

    public void onCancel(View view) {
        Intent intent = new Intent(getApplicationContext(), CheckInTasksView.class);
        startActivity(intent);
    }

    public void onAccept(View view) {
        Gson gson = new Gson();
        currentTask = gson.fromJson(gson.toJson(tasksInfo), CurrentTask.class);
        currentTask.tasksContent = new ArrayList<CheckInMarkerObject>();
        initMarkDatasById(tasksInfo.Id);
    }

    public void initMarkDatasById(String Id){
        Intent intent = new Intent(getApplicationContext(),TeamTracker.class);
        if(pref.contains(MarkTask.CURRENT_TASK.key)){
            String json = pref.getString(MarkTask.CURRENT_TASK.key, "");
            CurrentTask exsitTask = new Gson().fromJson(json, CurrentTask.class);
            Toast.makeText(getApplicationContext(),String.format("無法接取"),Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),String.format("因為你目前正在接取 %s 任務",exsitTask.taskTitle),Toast.LENGTH_SHORT).show();
            startActivity(intent);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task = db.collection(TaskSchema.Database.COLLECTION_NAME).document(Id).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ArrayList<DocumentReference>tasksContentReference = (ArrayList<DocumentReference>) doc.get("tasksContent");
                if(tasksContentReference != null){
                    for(int i = 0; i < tasksContentReference.size(); i++){
                        DocumentReference docr = tasksContentReference.get(i);
                        docr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                //TODO:一定要找到非同步存取資料的方法
                                currentTask.tasksContent.add(task.getResult().toObject(CheckInMarkerObject.class));
                                pref.edit().putString(MarkTask.CURRENT_TASK.key, new Gson().toJson(currentTask)).commit();
                            }
                        });
                    }
                }
            }
        });
        pref.edit().putString(MarkTask.CURRENT_TASK.key, new Gson().toJson(currentTask)).commit();
        Toast.makeText(getApplicationContext(),String.format("成功接取 %s 任務",currentTask.taskTitle),Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}