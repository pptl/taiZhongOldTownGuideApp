package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;


public class IntroductionCustomPopUpWin extends CustomPopUpWin {

    private TextView titleTV;
    private TextView contentTextView;
    private ImageView contentImageView;

    public IntroductionCustomPopUpWin(Context mContext, int xmlLayout, int i) {
        super(mContext, xmlLayout);
        titleTV = getView().findViewById(R.id.title_TextView);
        contentTextView =  getView().findViewById(R.id.contentTextView);
        contentImageView = getView().findViewById(R.id.contentImageView);

        //這裡可能要寫成看資料有多少再去找
        switch(i){
            case 0:
                titleTV.setText("hahha");
                contentTextView.setText("wawww");
                contentImageView.setImageResource(R.drawable.fivezeroone);
                break;
        }
    }
}
