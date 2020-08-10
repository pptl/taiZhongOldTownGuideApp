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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public personInfoPopWin(Activity activity, final Context mContext) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences pref = mContext.getSharedPreferences("userData",mContext.MODE_PRIVATE);
        teamID = pref.getString("teamID","error");
        teamName = pref.getString("teamName","error");

        final CollectionReference teamMemberCollectionRef = db.collection("teamID").document(teamID).collection("userData");

        this.view = LayoutInflater.from(mContext).inflate(R.layout.person_info_pop_win, null);
        textView = this.view.findViewById(R.id.personInfo_inviteCode_TextView);
        inviteCode = "團隊號碼："+ teamID;
        textView.setText(inviteCode);


        teamMemberCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                friendList.add(document.getId());
                                //Log.d("firebaseMember", String.valueOf(friendList.size()));
                                //Log.d("firebaseMember", document.getId() + " => " + document.getData().get("userName"));
                            }
                            mAdapter = new friendListRecycleViewAdapter(mContext,friendList,teamMemberCollectionRef);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d("firebaseMember", "Error getting documents: ", task.getException());
                        }
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