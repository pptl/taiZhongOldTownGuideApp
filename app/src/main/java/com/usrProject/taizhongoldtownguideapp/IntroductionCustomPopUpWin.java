package com.usrProject.taizhongoldtownguideapp;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;


public class IntroductionCustomPopUpWin extends CustomPopUpWin {

    private TextView titleTV;
    private TextView contentTextView;
    private ImageView contentImageView;

    public IntroductionCustomPopUpWin(Context mContext, int xmlLayout, int i) {
        super(mContext, xmlLayout,true);
        titleTV = getView().findViewById(R.id.title_TextView);
        contentTextView =  getView().findViewById(R.id.contentTextView);
        contentImageView = getView().findViewById(R.id.contentImageView);

        //這裡可能要寫成看資料有多少再去找
        switch(i){
            case 0:
                titleTV.setText(R.string.fivetwotwo_title);
                contentTextView.setText(R.string.fivetwotwo_content);
                contentImageView.setImageResource(R.drawable.fivetwotwo);
                break;
            case 1:
                titleTV.setText(R.string.fivezeroone_title);
                contentTextView.setText(R.string.fivezeroone_content);
                contentImageView.setImageResource(R.drawable.fivezeroone);
                break;
            case 2:
                titleTV.setText(R.string.fivezerosix_title);
                contentTextView.setText(R.string.fivezerosix_content);
                contentImageView.setImageResource(R.drawable.fivezerosix);
                break;
            case 3:
                titleTV.setText(R.string.fiveninenine_title);
                contentTextView.setText(R.string.fiveninenine_content);
                contentImageView.setImageResource(R.drawable.fiveninenine);
                break;
            case 4:
                titleTV.setText(R.string.fivetwofour_title);
                contentTextView.setText(R.string.fivetwofour_content);
                contentImageView.setImageResource(R.drawable.fivetwofour);
                break;
            case 5:
                titleTV.setText(R.string.fivethreesix_title);
                contentTextView.setText(R.string.fivethreesix_content);
                contentImageView.setImageResource(R.drawable.fivethreesix);
                break;
            default:
                titleTV.setText("No source");
                contentTextView.setText("No source");
        }
    }
}
