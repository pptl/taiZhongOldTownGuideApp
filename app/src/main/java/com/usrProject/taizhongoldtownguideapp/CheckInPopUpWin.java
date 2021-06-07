package com.usrProject.taizhongoldtownguideapp;

import android.content.Context;
import android.view.View;
import android.widget.Button;


public class CheckInPopUpWin extends CustomPopUpWin{

    public CheckInPopUpWin(Context mContext, int xmlLayout) {
        super(mContext, xmlLayout, false);
        Button closeWinBtn = this.getView().findViewById(R.id.check_in_record_pop_up_win_completed_close_btn);
        closeWinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
