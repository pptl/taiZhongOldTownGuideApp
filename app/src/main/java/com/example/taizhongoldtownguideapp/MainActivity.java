package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.GestureDetector;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 101;
    private GestureDetector GD;
    private ImageView imView;
    private ImageView imView2;
    private ImageView imView3;
    private ImageView imView4;
    private ImageView imView5;
    private ImageView imView6;
    private ImageView imView7;
    private SeekBar SB;
    private TextView TV;
    private TextView titleTV;
    private TextView contentTV;
    private TextView meibianzhiyuan;
    private ArrayList<String> imgList = null;

    private WindowManager.LayoutParams params;
    private float phoneWidthPixels;
    private float phoneHeightPixels;
    private float phoneDensity;
    private int curPointX;
    private int curPointY;
    private int weather;//1：晴天，2：陰天，3：小雨天，4： 雷雨天
    private SharedPreferences pref;
    private AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();

    public boolean clickFlag = true;

    //設置地圖上有效點擊範圍
    private int [][] objList={
            {654,277,764,372},//天主堂
            {327,209,453,337},//小學校
            {435,460,554,555},//武德館
            {560,115,645,233},//萬春宮
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("userData",MODE_PRIVATE);

        //請求獲取位置permission
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }

        //預設為晴天
        weather = 1;
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        //獲取手機高寬密度
        phoneDensity = metric.density;
        phoneHeightPixels = metric.heightPixels ;
        phoneWidthPixels = metric.widthPixels ;

        //用以記錄現在的點
        curPointX = 0;
        curPointY = 0;


        //宣告病初始化地圖
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeResource(getResources(),R.drawable.map_now,options);


        //宣告手勢
        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
        GD = new GestureDetector(MainActivity.this,androidGestureDectector);


        //設置滑軌監聽
        seekBarController();

        //用以存放各年份地圖名字
        imgList = new ArrayList<String>();

        //加入各年份地圖照片
        imgList.add("map_51");
        imgList.add("map_1911");
        imgList.add("map_1937");
        imgList.add("map_now");

        imView = (ImageView)this.findViewById(R.id.mapView);
        imView2 = (ImageView)findViewById(R.id.cloundView1);
        imView3 = (ImageView)findViewById(R.id.cloundView2);
        imView4 = (ImageView)findViewById(R.id.cloundView3);
        imView5 = (ImageView)findViewById(R.id.cloundView4);
        imView6 = (ImageView)findViewById(R.id.cloundView5);
        imView7 = (ImageView)findViewById(R.id.bgView);

        meibianzhiyuan = (TextView)this.findViewById(R.id.meibianzhiyuan_textView);

        //對地圖進行氣候管控
        //https://opendata.cwb.gov.tw/dist/opendata-swagger.html 中央氣象局開放opendata
        switch(weather){
            case 1:
                cloundController(imView2);
                cloundController(imView3);
                cloundController(imView4);
                cloundController(imView5);
                cloundController(imView6);
                imView7.setVisibility(View.INVISIBLE);
                break;
            case 2:
                String uri = "@drawable/black_clound";
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                imView2.setImageResource(imageResource);
                imView3.setImageResource(imageResource);
                imView4.setImageResource(imageResource);
                imView5.setImageResource(imageResource);
                imView6.setImageResource(imageResource);
                cloundController(imView2);
                cloundController(imView3);
                cloundController(imView4);
                cloundController(imView5);
                cloundController(imView6);
                imView7.setVisibility(View.VISIBLE);

        }



    }
    //彈出介紹視窗
    public void popWindow(int i) {
        titleTV = (TextView)findViewById(R.id.personInfo_inviteCode_TextView);
        contentTV = (TextView)findViewById(R.id.contentTextView);
        TakePhotoPopWin takePhotoPopWin = new TakePhotoPopWin(this,this,i);
        //设置Popupwindow显示位置（从底部弹出）
        takePhotoPopWin.showAtLocation(findViewById(R.id.mapView), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        params = getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha=0.7f;
        getWindow().setAttributes(params);


        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = getWindow().getAttributes();
                params.alpha=1f;
                getWindow().setAttributes(params);
            }
        });

    }

    public void goLocate(View view) {
        Boolean newUser = pref.getBoolean("inTeam",false);
        if(!newUser){
            Intent intent = new Intent(this,newUser.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this,whereIsMyFriend.class);
            startActivity(intent);
        }
    }


    //手勢控制，目前只做拖移
    class AndroidGestureDectector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if(clickFlag) {
                checkPointIf(e.getX()/phoneDensity,((e.getY()/phoneDensity)-80));
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {

            //showcurpoint();
            //設置scrollBar的animation
            Animation inAnim = new AlphaAnimation(0f,1.0f);
            Animation outAnim = new AlphaAnimation(1.0f,0f);
            inAnim.setDuration(500);
            outAnim.setDuration(500);
            inAnim.setFillAfter(true);
            outAnim.setFillAfter(true);

            //Log.d("showpoint",e.getX()+" , "+e.getY());
            if(e.getY() <= phoneHeightPixels * 0.7){
                if (SB.getVisibility() != View.INVISIBLE){
                    SB.startAnimation(outAnim);
                    TV.startAnimation(outAnim);
                    SB.setVisibility(View.INVISIBLE);
                    TV.setVisibility(View.INVISIBLE);
                }
            }
            if(e.getY() > phoneHeightPixels * 0.7){
                if(SB.getVisibility() != View.VISIBLE){
                    SB.startAnimation(inAnim);
                    TV.startAnimation(inAnim);
                    SB.setVisibility(View.VISIBLE);
                    TV.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int goX = (int)distanceX;
            int goY = (int)distanceY;
            imView.scrollBy(goX, goY);
            curPointX += goX;
            curPointY += goY;

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.d("seeEvent", String.valueOf(event));
        GD.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //對雲朵進行操控
    private void cloundController(ImageView imageView){
        String idString = imageView.getResources().getResourceEntryName(imageView.getId());
        switch (idString){
            case "cloundView1":
                Animation am1 = new TranslateAnimation(1000f,-800f,0f,0f);
                am1.setDuration(55000);
                am1.setRepeatCount(-1);
                imageView.startAnimation(am1);
                break;
            case "cloundView2":
                Animation am2 = new TranslateAnimation(1800f,-800f,900f,900f);
                am2.setDuration(55000);
                am2.setRepeatCount(-1);
                am2.setStartTime(100000);
                imageView.startAnimation(am2);
                break;
            case "cloundView3":
                Animation am3 = new TranslateAnimation(1400f,-800f,200f,200f);
                am3.setDuration(50000);
                am3.setRepeatCount(-1);
                imageView.startAnimation(am3);
                break;
            case "cloundView4":
                Animation am4 = new TranslateAnimation(1500f,-800f,800f,800f);
                am4.setDuration(50000);
                am4.setRepeatCount(-1);
                imageView.startAnimation(am4);
                break;
            case "cloundView5":
                Animation am5 = new TranslateAnimation(1200f,-800f,700f,700f);
                am5.setDuration(50000);
                am5.setRepeatCount(-1);
                am5.setStartTime(100000);
                imageView.startAnimation(am5);
                break;
        }
    }
    //監控地圖上制定地點有效區用
    private void checkPointIf(float xPoint, float yPoint){
        double finalPointX = xPoint + curPointX/phoneDensity;
        double finalPointY = yPoint + curPointY/phoneDensity;
        //Log.d("showPop",finalPointX+" , " +finalPointY);
        for(int i=0; i<objList.length; i++){
            //Log.d("showPop","true");
            if(finalPointX > objList[i][0] && finalPointY > objList[i][1]){
                if(finalPointX < objList[i][2] && finalPointY < objList[i][3]){
                    popWindow(i);
                    break;
                }
            }
        }
    }

    //拖移bar控制
    private void seekBarController(){

        SB = (SeekBar)findViewById(R.id.seekBar);
        TV = (TextView)findViewById(R.id.yearTextView);
        SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if(progress<=25){
                    SB.setProgress(0);
                    TV.setText("乾隆40~51年");
                    changeImage(0);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 25 && progress <= 50){
                    SB.setProgress(38);
                    TV.setText("1911年");
                    changeImage(1);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 50 && progress <= 75 ){
                    SB.setProgress(63);
                    TV.setText("1937年");
                    changeImage(2);
                    meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 75 ){
                    SB.setProgress(100);
                    TV.setText("2020年");
                    changeImage(3);
                    meibianzhiyuan.setText(R.string.laoshi_meibianzhiyuan);
                    clickFlag = true;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Log.d("onTouch","ontouch");

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.d("onTouch","onstop");
            }
        });

    }

    //更換地圖用
    private void changeImage(int i){
        imView = (ImageView)findViewById(R.id.mapView);
        String uri = "@drawable/" + imgList.get(i);
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        imView.setImageResource(imageResource);
    }

}

