package com.usrProject.taizhongoldtownguideapp.schema.type;

import android.content.Context;

import com.usrProject.taizhongoldtownguideapp.R;

public enum MapType {
    MAP_51,
    MAP_1911,
    MAP_1937,
    MAP_NOW;

//            {540, 507},//map_51
//            {540, 415},//map_1911
//            {540, 433},//map_1937
//            {960, 768}//map_now
    public int resId;
    public int x,y;
    MapType(){
        switch (this){
            case MAP_51:
                resId = R.drawable.map_51;
                x = 540;
                y = 507;
                break;
            case MAP_1911:
                resId = R.drawable.map_1911;
                x = 540;
                y = 415;
                break;
            case MAP_1937:
                resId = R.drawable.map_1937;
                x = 540;
                y = 433;
                break;
            case MAP_NOW:
                resId = R.drawable.map_now;
                x = 960;
                y = 768;
                break;
        }
    }
}
