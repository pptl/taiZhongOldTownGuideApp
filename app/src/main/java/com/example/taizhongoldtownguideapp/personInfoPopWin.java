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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class personInfoPopWin extends PopupWindow {

    private View view;
    private PopupWindow popUpWin;
    private List<String> friendList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private friendListRecycleViewAdapter mAdapter;
    private String teamID;
    private String teamName;
    private TextView textView;
    private String inviteCode;
    private SharedPreferences pref;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamMemberRef;


    public personInfoPopWin(Activity activity, final Context mContext, final GoogleMap map) {
        mDatabase = FirebaseDatabase.getInstance();
        pref = mContext.getSharedPreferences("userData",mContext.MODE_PRIVATE);
        teamID = pref.getString("teamID","error");
        teamName = pref.getString("teamName","error");

        teamMemberRef = mDatabase.getReference().child("team").child(teamID).child("userData");

        this.view = LayoutInflater.from(mContext).inflate(R.layout.person_info_pop_win, null);
        textView = this.view.findViewById(R.id.personInfo_inviteCode_TextView);
        inviteCode = "團隊號碼："+ teamID;
        textView.setText(inviteCode);


        teamMemberRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                friendList.add(snapshot.getKey());
                mAdapter = new friendListRecycleViewAdapter(mContext,friendList,teamMemberRef, map);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Log.d("firebaseMenber",teamMemberDocumentRef.document().toString());
        //这里依靠房间名字找朋友放进矩阵

        mRecyclerView = this.view.findViewById(R.id.showFriend_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));






        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.personinfo_popwindow_layout).getTop();
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