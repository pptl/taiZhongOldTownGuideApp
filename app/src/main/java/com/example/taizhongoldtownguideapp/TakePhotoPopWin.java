package com.example.taizhongoldtownguideapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
    private PopupWindow popUpWin;
    private ImageView contentIV;


    public TakePhotoPopWin(Activity activity, Context mContext, int i) {
        this.view = LayoutInflater.from(mContext).inflate(R.layout.take_photo_pop, null);
        titleTV = (TextView) this.view.findViewById(R.id.titleTextView);
        contentTV = (TextView) this.view.findViewById(R.id.contentTextView);
        contentIV = (ImageView)this.view.findViewById(R.id.contentImageView);


        switch(i){
            case 0:
                titleTV.setText(R.string.tianzhutang_title);
                contentTV.setText(R.string.tianzhutang_content);
                contentIV.setImageResource(R.drawable.chuanting);
                break;
            case 1:
                titleTV.setText(R.string.xiaoxuexiao_title);
                contentTV.setText(R.string.xiaoxuexiao_content);
                contentIV.setImageResource(R.drawable.huochezhan);
                break;
            case 2:
                titleTV.setText(R.string.wudeguan_title);
                contentTV.setText(R.string.wudeguan_content);
                contentIV.setImageResource(R.drawable.shiyisuo);
                break;
            case 3:
                titleTV.setText(R.string.wancungong_title);
                contentTV.setText(R.string.wancungong_content);
                contentIV.setImageResource(R.drawable.wancungong);
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
        this.setAnimationStyle(R.style.take_photo_anim);

    }

}