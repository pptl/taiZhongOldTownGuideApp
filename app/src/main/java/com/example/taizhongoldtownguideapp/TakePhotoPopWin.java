package com.example.taizhongoldtownguideapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TakePhotoPopWin extends PopupWindow {

    private View view;
    private TextView titleTV;
    private TextView contentTV;
    private ImageView contentIV;


    public TakePhotoPopWin(Context mContext, int i) {
        this.view = LayoutInflater.from(mContext).inflate(R.layout.take_photo_pop, null);
        titleTV = (TextView) this.view.findViewById(R.id.personInfo_inviteCode_TextView);
        contentTV = (TextView) this.view.findViewById(R.id.contentTextView);
        contentIV = (ImageView)this.view.findViewById(R.id.contentImageView);

        Log.d("seeThing", String.valueOf(R.layout.take_photo_pop));


        switch(i){
            case 0:
                titleTV.setText(R.string.fivezeroone_title);
                contentTV.setText(R.string.fivezeroone_content);
                contentIV.setImageResource(R.drawable.fivezeroone);
                break;
        }
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.popwindow_layout).getTop();
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
        this.setAnimationStyle(R.style.pop_up_win_anim);

    }

}