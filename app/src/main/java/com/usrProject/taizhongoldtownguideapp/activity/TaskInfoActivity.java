package com.usrProject.taizhongoldtownguideapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TaskInfoActivity extends AppCompatActivity {
    private CheckTasks tasksInfo;
    private TextView taskTitleView;
    private TextView taskDescView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
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
        CurrentTask currentTask = gson.fromJson(gson.toJson(tasksInfo), CurrentTask.class);
        currentTask.tasksContent = new ArrayList<CheckInMarkerObject>();
        initMarkDatasById(tasksInfo.Id, currentTask.tasksContent);
    }

    public void initMarkDatasById(String Id, final List<CheckInMarkerObject> tasksContent){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task = db.collection(TaskSchema.Database.COLLECTION_NAME).document(Id).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ArrayList<DocumentReference>tasksContentReference = (ArrayList<DocumentReference>) doc.get("tasksContent");
//              防呆機制
                if(tasksContentReference != null){
                    for(DocumentReference documentReference: tasksContentReference){
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    tasksContent.add(task.getResult().toObject(CheckInMarkerObject.class));
                                }
                            }
                        });
                    }
                }

                Gson gson = new Gson();
                CurrentTask currentTask = gson.fromJson(gson.toJson(tasksInfo),CurrentTask.class);
                currentTask.tasksContent = tasksContent;
                SharedPreferences userData = getPreferences(MODE_PRIVATE);
                if(userData.contains(MarkTask.CURRENT_TASK.key)){
                    String json = new String();
                    json = userData.getString(MarkTask.CURRENT_TASK.key,"");
                    CurrentTask temp = gson.fromJson(json, CurrentTask.class);
                    Toast.makeText(getApplicationContext(),"你已經接取任務：" + temp.taskTitle,Toast.LENGTH_LONG).show();
                }else{
                    userData.edit().putString(MarkTask.CURRENT_TASK.key,gson.toJson(currentTask)).commit();
                    Toast.makeText(getApplicationContext(),"確定接取任務",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), TeamTracker.class);
                    startActivity(intent);
                }

            }
        });
    }
}