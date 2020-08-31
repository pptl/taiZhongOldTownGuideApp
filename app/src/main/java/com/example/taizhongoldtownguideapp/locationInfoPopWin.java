package com.example.taizhongoldtownguideapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class locationInfoPopWin extends PopupWindow {

    private View view;
    private PopupWindow popUpWin;
    private List<String> locationList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private locationListRecycleViewAdapter mAdapter;
    private String teamID;
    private SharedPreferences pref;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamMarkerRef;


    public locationInfoPopWin(Activity activity, final Context mContext, final GoogleMap map) {
        this.view = LayoutInflater.from(mContext).inflate(R.layout.location_info_pop_win, null);

        mDatabase = FirebaseDatabase.getInstance();

        pref = mContext.getSharedPreferences("userData",mContext.MODE_PRIVATE);
        teamID = pref.getString("teamID","error");
        //mDatabase.getReference().child("team").child(teamID);

        if(mDatabase.getReference().child("team").child(teamID).child("marker") != null) {
            teamMarkerRef = mDatabase.getReference().child("team").child(teamID).child("marker");
            mRecyclerView = this.view.findViewById(R.id.showLocation_recyclerView);
            //mAdapter = new locationListRecycleViewAdapter(mContext,locationList,teamMarkerRef,map);
            //mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            teamMarkerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    locationList.add(snapshot.getKey());
                }
                mAdapter = new locationListRecycleViewAdapter(mContext,locationList,teamMarkerRef,map);
                mRecyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        }


        //final CollectionReference markCollectionRef = db.collection("teamID").document(teamID).collection("mark");



        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.locationinfo_popwindow_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);

    }

}