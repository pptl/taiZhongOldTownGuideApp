package com.usrProject.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.usrProject.taizhongoldtownguideapp.model.CheckTasks;

import java.util.ArrayList;

public class CheckInTasksView extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_item_view);
        RecyclerView tasksItemsList = findViewById(R.id.tasksList);
        tasksItemsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        tasksItemsList.setLayoutManager(layoutManager);
        //        mDatabase = FirebaseDatabase.getInstance();
        ArrayList<CheckTasks> testDataSet = new ArrayList<>();
        testDataSet.add(new CheckTasks("Test1","wwwww"));
        testDataSet.add(new CheckTasks("Tes22t1", "qqqqq"));
        adapter = new TaskAdapter(testDataSet);
        tasksItemsList.setAdapter(adapter);

    }



//  建立調配氣
    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
        private ArrayList<CheckTasks> dataset;
        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView taskTitle;
            public TextView taskDesc;
            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                taskTitle = view.findViewById(R.id.taskTitle);
                taskDesc = view.findViewById(R.id.taskDesc);
            }


        }
//      規定調配器一定要從外部資料傳入才行
        public TaskAdapter(ArrayList<CheckTasks> dataset){
            this.dataset = dataset;
        }

        @NonNull
        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.checkin_task_recycle_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
            holder.taskTitle.setText(dataset.get(position).taskTitle);
            holder.taskDesc.setText(dataset.get(position).taskDesc);
        }

        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }
}
