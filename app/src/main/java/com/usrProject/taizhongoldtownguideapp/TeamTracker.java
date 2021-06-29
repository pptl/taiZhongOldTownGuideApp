package com.usrProject.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.usrProject.taizhongoldtownguideapp.activity.CheckInTasksView;
import com.usrProject.taizhongoldtownguideapp.model.CheckIn.CheckInMarkerObject;
import com.usrProject.taizhongoldtownguideapp.schema.PopWindowType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TeamTracker extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mCurrentLocation = null;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private WindowManager.LayoutParams params;
    private String teamID;
    private String userID;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamRef;
    private DatabaseReference usersRef;
    private DatabaseReference markersRef;
    private DatabaseReference checkInMarkerRef;
    private Timer timer;
    private SharedPreferences pref;
    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;
    private Handler messageHandler = null;
    private String responseJsonString = "";
    HashMap<String, Marker> hashMapMarker = new HashMap<>();
    HashMap<String, Marker> foodMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> shoppingMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> roomMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> historyMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> playMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> trafficMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> serviceMarkerHashMap = new HashMap<>();
    HashMap<String, Marker> religionMarkerHashMap = new HashMap<>();
    private String url = "http://140.134.48.76/USR/API/API/Default/APPGetData?name=point&token=2EV7tVz0Pv6bLgB/aXRURg==";
    private Button switchLayerBtn;
    private Button checkInRecordBtn;
    private Button demoBtn;
    Set<String> checkedLayerSet = new HashSet<>();
    private String roomType;
    private Button locationInfoButton;
    private Button personInfoButton;
    private Boolean isExiting = false;//判斷使用者是否正在退出團隊
    private ArrayList<CheckInMarkerObject> checkInMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_tracker);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pref = getSharedPreferences("userData", MODE_PRIVATE);

        teamID = pref.getString("teamID", "000000");
        userID = pref.getString("userID", "null");

        personInfoButton = findViewById(R.id.whereIsMyFriend_person_btn);
        locationInfoButton = findViewById(R.id.whereIsMyFriend_location_btn);
        switchLayerBtn = findViewById(R.id.layer_btn);
        checkInRecordBtn = findViewById(R.id.checkIn_record_btn);
        demoBtn = findViewById(R.id.demo_btn);


        //roomType 分"singleUser"和"multiUsers"用來區別是單人使用或者多人使用的地圖
        roomType = pref.getString("roomType", "multiUsers");
        //如果是單人地圖的話，需要處理按鈕佈局

        if (roomType != null) {
            if (roomType.equals("singleUser")) {
                personInfoButton.setBackgroundResource(R.drawable.exit_icon);
            }
        }
//      打卡任務列表
        checkInRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckInTasksView.class);

                startActivity(intent);

                /*
                pref.edit().putString("nextStopTitle","FengChia").apply();
                pref.edit().putString("nextStopContent","The FengChia univercity is a univercity in TaiChung").apply();
                pref.edit().putInt("checkInCompleted",0).apply();
                */

            }
        });

        demoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow(PopWindowType.CHECK_IN_COMPLETED);
            }
        });

        locationInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow(PopWindowType.LOCATION_INFO);
            }
        });

        personInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roomType.equals("singleUser")) {
                    exitTeam();
                } else {
                    popWindow(PopWindowType.PERSON_INFO);
                }
            }
        });

        //預設popupwin裡的checkbox history元件是已經勾選的
        checkedLayerSet.add("history");
        //存起來，別的layout會用到
        pref.edit().putStringSet("checkedLayer", checkedLayerSet).apply();

        timer = new Timer();
        //固定每5秒檢查用戶坐標是否有移動
        timer.schedule(checkTask, 1000, 5000);

        mDatabase = FirebaseDatabase.getInstance();
        teamRef = mDatabase.getReference("team").child(teamID);
//        打卡系統
        checkInMarkerRef = mDatabase.getReference("checkInMarker");
        usersRef = teamRef.child("userData");
        markersRef = teamRef.child("marker");

        switchLayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow(PopWindowType.SWITCH_LAYER);
            }
        });

    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(TeamTracker.this));
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                LatLng position = marker.getPosition();
                addLocation(position.latitude, position.longitude);
            }
        });

        messageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String jsonString = JsonParser.parseString(responseJsonString).getAsString();

                    try {
                        JSONArray jsonArray = new JSONArray(jsonString);
                        Double xPoint = 0.0;
                        Double yPoint = 0.0;
                        String title = "";
                        String type = "";
                        String content = "";
                        String id = "";
                        float markerColor = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            xPoint = Double.parseDouble(jsonObject.get("PO_X").toString());
                            yPoint = Double.parseDouble(jsonObject.get("PO_Y").toString());
                            title = jsonObject.get("PO_TITLE").toString();
                            type = jsonObject.get("PO_TYPES").toString();
                            content = jsonObject.get("PO_CONTENT").toString();
                            id = jsonObject.get("PO_ID").toString();
                            Marker marker = null;

                            switch (Integer.parseInt(type)) {
                                case 0://美食
                                    markerColor = BitmapDescriptorFactory.HUE_AZURE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("food");
                                    foodMarkerHashMap.put(id, marker);
                                    break;
                                case 1://購物
                                    markerColor = BitmapDescriptorFactory.HUE_BLUE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("shopping");
                                    shoppingMarkerHashMap.put(id, marker);
                                    break;
                                case 2://住宿
                                    markerColor = BitmapDescriptorFactory.HUE_CYAN;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("room");
                                    roomMarkerHashMap.put(id, marker);
                                    break;
                                case 3://歷史
                                    markerColor = BitmapDescriptorFactory.HUE_RED;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setTag("history");
                                    historyMarkerHashMap.put(id, marker);
                                    break;
                                case 4://遊憩
                                    markerColor = BitmapDescriptorFactory.HUE_MAGENTA;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("play");
                                    playMarkerHashMap.put(id, marker);
                                    break;
                                case 5://交通
                                    markerColor = BitmapDescriptorFactory.HUE_ORANGE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("traffic");
                                    trafficMarkerHashMap.put(id, marker);
                                    break;
                                case 6://服務
                                    markerColor = BitmapDescriptorFactory.HUE_GREEN;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("service");
                                    serviceMarkerHashMap.put(id, marker);
                                    break;
                                case 7://宗教
                                    markerColor = BitmapDescriptorFactory.HUE_ROSE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    marker.setTag("religion");
                                    religionMarkerHashMap.put(id, marker);
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("seeMarkerLoad", "markerLoadFail");
                    }
                }
                ;
            }
        };
        //獲得自己裝置的位置
        getDeviceLocation();
        //使用坐標資料api
        getPointJson(url);

        //firebase上預設可打卡的地標
//        checkInMarkerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Marker marker = null;
////              將可打卡座標顯示在map上面，並使用物件儲存
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    String markTitle = data.child("markTitle").getValue(String.class);
//                    String markContext = data.child("markContent").getValue(String.class);
//                    Double markLatitude = data.child("markLatitude").getValue(Double.class);
//                    Double markLongitude = data.child("markLongitude").getValue(Double.class);
//
//                    if (markTitle != null && markContext != null && markLatitude != null && markLongitude != null) {
//                        if (checkInMarkers == null) {
//                            checkInMarkers = new ArrayList<>();
//                        }
//                        checkInMarkers.add(new CheckInMarkerObject(markTitle, markContext, markLatitude, markLongitude));
//                        Bitmap markerBitmap = new BitmapFactory().decodeResource(getResources(), getResources().getIdentifier("marker_sm", "drawable", getPackageName()));
//                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(markLatitude, markLongitude)).title(markTitle).icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));
//                        marker.setTag("checkIn");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        //每次fireBase裡朋友資料更新時，更新本地朋友資料
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Marker marker = null;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String userName = data.child("userName").getValue(String.class);
                    String userIconPath = data.child("userIconPath").getValue(String.class);
                    String userID = data.getKey();

                    if (userName != null && userIconPath != null && userID != null) {
                        int iconPathID = getResources().getIdentifier(userIconPath, "drawable", getPackageName());
                        Bitmap userBitmap = new BitmapFactory().decodeResource(getResources(), iconPathID);
                        Double userLatitude = data.child("userLatitude").getValue(Double.class);
                        Double userLongitude = data.child("userLongitude").getValue(Double.class);

                        if (userLatitude != null && userLongitude != null) {
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(userLatitude, userLongitude)).title(userName).icon(BitmapDescriptorFactory.fromBitmap(userBitmap)));
                            marker.setTag("user");
                            if (hashMapMarker.containsKey(userID)) {
                                Marker delMarker = hashMapMarker.get(userID);
                                delMarker.remove();
                                hashMapMarker.remove(userID);
                            }
                            hashMapMarker.put(userID, marker);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //每次firebase裡有marker更新時，更新本地所有marker資料
        markersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String markContext = data.child("markContext").getValue(String.class);
                        //String userIconPath = data.child("userIconPath").getValue(String.class);
                        Double markLatitude = data.child("markLatitude").getValue(Double.class);
                        Double markLongitude = data.child("markLongitude").getValue(Double.class);

                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(markLatitude, markLongitude)).title(markContext));
                        marker.setTag("customize");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    //等待使用者在createNewMarker頁面把增加marker的資訊返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("markContext");
                Double latitude = data.getDoubleExtra("latitude", 0);
                Double longitude = data.getDoubleExtra("longitude", 0);
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(returnString).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                marker.setTag("customize");
            }
        }
    }

    //獲取使用者裝置現在的位置
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
        if (!isExiting) {
            location.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Map<String, Object> userLocations = new HashMap<>();
                    DatabaseReference myRef = usersRef.child(userID);
                    mCurrentLocation = (Location) location;

                    if (mCurrentLocation != null) {

                        //檢查user有沒有移動
                        userLocations.put("userLatitude", mCurrentLocation.getLatitude());
                        userLocations.put("userLongitude", mCurrentLocation.getLongitude());

                        myRef.updateChildren(userLocations);
                        //地圖addMarker時可以使用到
                        pref.edit().putLong("mLatitude", Double.doubleToLongBits(location.getLatitude())).apply();
                        pref.edit().putLong("mLongitude", Double.doubleToLongBits(location.getLongitude())).apply();

                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), 15f);
                    } else {
                        //如果用戶進入app後才開啟GPS定位的話，會需要重啟location的資料才會正常
                        finish();
                        Intent intent = new Intent(getApplicationContext(), Loading.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void checkLocationChange() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        final float preLatitude = pref.getFloat("userLatitude",0);
        final float preLongitude = pref.getFloat("userLongitude",0);

        if(!isExiting){
            location.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mCurrentLocation = (Location) location;
                    if(mCurrentLocation != null){
                        //檢查位置
                        if(preLatitude != (float)mCurrentLocation.getLatitude() || preLongitude != (float)mCurrentLocation.getLongitude()){
                            Map<String, Object> userLocations = new HashMap<>();
                            userLocations.put("userLatitude",mCurrentLocation.getLatitude());
                            userLocations.put("userLongitude",mCurrentLocation.getLongitude());
                            //地圖addMarker時可以使用到
                            pref.edit().putLong("mLatitude",Double.doubleToLongBits(location.getLatitude())).apply();
                            pref.edit().putLong("mLongitude",Double.doubleToLongBits(location.getLongitude())).apply();
                            usersRef.child(userID).updateChildren(userLocations);
                        }
                        //計算最靠近的checkPoint
//                        if(pref.getInt("checkInCompleted",0) < 5){
//                            double minDistance = 9999;
//                            int minIndex = 0;
//                            double distance = 0;
//
//                            for(int j=0;j<5;j++){
//                                //兩點公式
//                                if(!checkInMarkerObjectList[j].isChecked()){
//                                    distance = Math.abs(Math.sqrt(Math.pow(mCurrentLocation.getLongitude() - checkInMarkerObjectList[j].getMarkLongitude(),2) + Math.pow(mCurrentLocation.getLatitude() - checkInMarkerObjectList[j].getMarkLatitude(),2)));
//                                    if(distance < minDistance){
//                                        minDistance = distance;
//                                        minIndex = j;
//                                    }
//                                }
//                            }
//                            if (minDistance < 2){
//                                checkInMarkerObjectList[minIndex].setChecked(true);
//                                pref.edit().putInt("checkInCompleted",pref.getInt("checkInCompleted", 0) + 1).apply();
//                                popWindow("checkInCompleted");
//                            }
//
//                            pref.edit().putString("nextStopTitle",checkInMarkerObjectList[minIndex].getMarkTitle()).apply();
//                            pref.edit().putString("nextStopContent",checkInMarkerObjectList[minIndex].getMarkContent()).apply();
//                        }

                    }
                }
            });
        }
    }

    private TimerTask checkTask = new TimerTask() {
        @Override
        public void run() {
            checkLocationChange();
        }
    };

    //用來移動你的攝像機
    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    public void popWindow(PopWindowType popWindowType) {
        if(popWindowType == PopWindowType.LOCATION_INFO){
            LocationInfoPopUpWin locationInfoPopWin = new LocationInfoPopUpWin(this, R.layout.location_info_pop_win, mMap, this);
            locationInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            locationInfoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        } else if (popWindowType == PopWindowType.PERSON_INFO){
            PersonInfoPopUpWin personInfoPopWin = new PersonInfoPopUpWin(this, R.layout.person_info_pop_win, mMap);
            personInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
           personInfoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        } else if (popWindowType == PopWindowType.SWITCH_LAYER){
            SwitchLayerPopUpWin switchLayerPopUpWin = new SwitchLayerPopUpWin(this, R.layout.switch_layer_pop_up_win);
            switchLayerPopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            switchLayerPopUpWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        }else if (popWindowType==PopWindowType.CHECK_IN_COMPLETED){

            CheckInPopUpWin checkInPopUpWin = new CheckInPopUpWin(this,R.layout.check_in_completed_pop_up_win, pref.getInt("checkInCompleted", 0), pref.getString("nextStopTitle",""));
            checkInPopUpWin.showAtLocation(findViewById(R.id.map), Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            params.alpha = 0.7f;
            getWindow().setAttributes(params);
            checkInPopUpWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha = 1f;
                    getWindow().setAttributes(params);
                }
            });
        }
    }

    public void exitTeam() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("是否要退出團隊？");
        alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //isLeader ,roomnumber
                isExiting = true;
                pref.edit().putBoolean("inTeam",false).commit();
                usersRef.child(userID).removeValue();
                alert.setView(null);
                //這裡要去firebase刪掉相關用戶的資料，現在還沒實作
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.create().show();
    }
    //給其他layout用的
    public void exitTeam(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("是否要退出團隊？");
        alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isExiting = true;
                pref.edit().putBoolean("inTeam",false).commit();
                //這裡要去firebase刪掉相關用戶的資料
                usersRef.child(userID).removeValue();
                alert.setView(null);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    public void addLocation(double latitude, double longitude) {
        //pref.edit().putFloat("Latitude",(float)mCurrentLocation.getLatitude()).putFloat("Longitude",(float)mCurrentLocation.getLongitude()).commit();
        params = getWindow().getAttributes();
        params.alpha=1f;
        getWindow().setAttributes(params);
        Intent intent = new Intent(this, CreateNewMarker.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivityForResult(intent,ADD_LOCATION_ACTIVITY_REQUEST_CODE);
    }
    void getPointJson(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    responseJsonString = response.body().string();
                    Message msg = new Message();
                    msg.what = 1;
                    messageHandler.sendMessage(msg);
                }
            }
        });

    }

    public void switchLayer(View view) {

        boolean checked = ((CheckBox) view).isChecked();
        String boxName = "";
        switch(view.getId()) {
            case R.id.foodCheckBox:
                boxName = "food";
                for(Map.Entry<String, Marker> entry : foodMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.shoppingCheckBox:
                boxName = "shopping";
                for(Map.Entry<String, Marker> entry : shoppingMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.roomCheckBox:
                boxName = "room";
                for(Map.Entry<String, Marker> entry : roomMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.historyCheckBox:
                boxName = "history";
                for(Map.Entry<String, Marker> entry : historyMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.playCheckBox:
                boxName = "play";
                for(Map.Entry<String, Marker> entry : playMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.trafficCheckBox:
                boxName = "traffic";
                for(Map.Entry<String, Marker> entry : trafficMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.serviceCheckBox:
                boxName = "service";
                for(Map.Entry<String, Marker> entry : serviceMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
            case R.id.religionCheckBox:
                boxName = "religion";
                for(Map.Entry<String, Marker> entry : religionMarkerHashMap.entrySet()){ entry.getValue().setVisible(checked);}
                break;
        }
        if(checked){
            checkedLayerSet.add(boxName);
        } else {
            checkedLayerSet.remove(boxName);
        }
        pref.edit().putStringSet("checkedLayer", checkedLayerSet).apply();

    }
}
