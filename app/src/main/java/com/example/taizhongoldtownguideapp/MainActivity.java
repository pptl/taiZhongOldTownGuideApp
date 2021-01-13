package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import com.google.gson.JsonObject;
import com.plattysoft.leonids.ParticleSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    //private TextView meibianzhiyuan;
    private ArrayList<String> imgList = null;

    private WindowManager.LayoutParams params;
    private float phoneWidthPixels;
    private float phoneHeightPixels;
    private float phoneDensity;
    private int curPointX;
    private int curPointY;
    private String weather;//1：晴天，2：陰天，3：小雨天，4： 雷雨天
    private SharedPreferences pref;
    private AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
    private Handler handler;

    public boolean clickFlag = true;

    //設置地圖上有效點擊範圍
    private int [][] objList={
            //{654,277,764,372},//天主堂
            //{327,209,453,337},//小學校
            //{435,460,554,555},//武德館
            //{560,115,645,233},//萬春宮
            {860,1016,916,1058},//彰化銀行繼光街宿舍
    };

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("userData",MODE_PRIVATE);



        getWeather();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    imView2 = (ImageView)findViewById(R.id.cloundView1);
                    imView3 = (ImageView)findViewById(R.id.cloundView2);
                    imView4 = (ImageView)findViewById(R.id.cloundView3);
                    imView5 = (ImageView)findViewById(R.id.cloundView4);
                    imView6 = (ImageView)findViewById(R.id.cloundView5);
                    imView7 = (ImageView)findViewById(R.id.bgView);
                    weather = "晴";
                    if(weather.equals("陰")){
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
                    } else if(weather.equals(weather.equals("陰带雨")) || weather.equals("雨")){
                        String uri = "@drawable/rain_effect";
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        imView7.setVisibility(View.VISIBLE);
                        imView7.setImageResource(imageResource);
                    }
                    else{
                        cloundController(imView2);
                        cloundController(imView3);
                        cloundController(imView4);
                        cloundController(imView5);
                        cloundController(imView6);
                        imView7.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

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

        //meibianzhiyuan = (TextView)this.findViewById(R.id.meibianzhiyuan_textView);

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
        //請求獲取位置permission
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        else{
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //这里其实要出现一个视窗提醒使用者开GPS
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }


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


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("seePermition", String.valueOf(PackageManager.PERMISSION_GRANTED));
        if (requestCode == REQUEST_CODE){
            Log.d("seePermition","permitionOK");

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                //这里其实要出现一个视窗提醒使用者开GPS
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }


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
        else{
            Log.d("seePermition","permitionNotOK");
        }
    }

    public void goMorePost(View view) {
        Intent intent = new Intent(this,crawlersPage.class);
        startActivity(intent);
    }

    public void goSurroundView(View view) {
        Intent intent = new Intent(this, surroundView.class);
        startActivity(intent);
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
                    //meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 25 && progress <= 50){
                    SB.setProgress(38);
                    TV.setText("1911年");
                    changeImage(1);
                    //meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 50 && progress <= 75 ){
                    SB.setProgress(63);
                    TV.setText("1937年");
                    changeImage(2);
                    //meibianzhiyuan.setText(R.string.meibianzhiyuan);
                    clickFlag = false;

                }else if(progress > 75 ){
                    SB.setProgress(100);
                    TV.setText("2020年");
                    changeImage(3);
                    //meibianzhiyuan.setText(R.string.laoshi_meibianzhiyuan);
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
        /*
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), imageResource, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        Log.d("seeImgRes", String.valueOf(imageHeight));
        Log.d("seeImgRes", String.valueOf(imageWidth));
        Log.d("seeImgRes", String.valueOf(imageType));
        */
    }
    private void getWeather(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String data = getJSON("https://opendata.cwb.gov.tw/api/v1/rest/datastore/O-A0003-001?Authorization=CWB-55466E79-2D5C-4102-B476-5B001C263F2A&elementName=Weather&parameterName=CITY",9000);
                    //Log.d("seeData",data);
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArr = jsonObject.getJSONObject("records").getJSONArray("location");

                    //過濾只獲取台中的資料
                    JSONObject taizhongData = new JSONObject();
                    for(int i=0;i< jsonArr.length(); i++){
                        JSONObject oneObject = null;
                        try {
                            oneObject = jsonArr.getJSONObject(i);
                            if(oneObject.getString("locationName").equals("臺中"))
                            {
                                taizhongData = oneObject;
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    weather = taizhongData.getJSONArray("weatherElement").getJSONObject(0).getString("elementValue");

                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-D0047-073?Authorization=CWB-55466E79-2D5C-4102-B476-5B001C263F2A
    /*
    private Runnable multiThread = new Runnable() {
        @Override
        public void run() {
            try {
                String data = getJSON("https://opendata.cwb.gov.tw/api/v1/rest/datastore/O-A0003-001?Authorization=CWB-55466E79-2D5C-4102-B476-5B001C263F2A&elementName=Weather&parameterName=CITY",9000);
                //Log.d("seeData",data);
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArr = jsonObject.getJSONObject("records").getJSONArray("location");

                //過濾只獲取台中的資料
                JSONObject taizhongData = new JSONObject();
                for(int i=0;i< jsonArr.length(); i++){
                    JSONObject oneObject = null;
                    try {
                        oneObject = jsonArr.getJSONObject(i);
                        if(oneObject.getString("locationName").equals("臺中"))
                        {
                            taizhongData = oneObject;
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String weather = taizhongData.getJSONArray("weatherElement").getJSONObject(0).getString("elementValue");
                Log.d("seeData",weather);

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    };
    */

    public static String getJSON(String url, int timeout) throws IOException {

        URL u = new URL(url);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        c.setRequestProperty("Accept", "application/json");

        c.setRequestMethod("GET");

        //c.setUseCaches(false);
        //c.setAllowUserInteraction(false);
        c.setConnectTimeout(timeout);   //设置连接主机超时（单位：毫秒）
        c.setReadTimeout(timeout);      //设置从主机读取数据超时（单位：毫秒）
        //c.setRequestProperty("User-Agent","Mozilla/5.0");
        c.connect();
        int status = c.getResponseCode();

        switch (status) {
            case 200:
            case 201:

                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(),"utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
        }

        return null;
    }

}

