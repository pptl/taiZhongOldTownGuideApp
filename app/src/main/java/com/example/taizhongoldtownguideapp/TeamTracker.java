package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TeamTracker extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mCurrentLocation;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private WindowManager.LayoutParams params;
    private String teamID;
    private String userID;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamRef;
    private DatabaseReference usersRef;
    private DatabaseReference markersRef;
    private Timer timer;
    private SharedPreferences pref;
    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;
    private Handler messageHandler = null;
    private String responseJsonString = "";
    HashMap<String,Marker> hashMapMarker = new HashMap<>();
    HashMap<String,Marker> foodMarkerHashMap = new HashMap<>();
    HashMap<String,Marker> shoppingMarkerHashMap = new HashMap<>();
    HashMap<String,Marker> roomMarkerHashMap = new HashMap<>();
    HashMap<String,Marker> historyMarkerHashMap = new HashMap<>();
    HashMap<String,Marker> playMarkerHashMap = new HashMap<>();
    HashMap<String,Marker> trafficMarkerHashMap = new HashMap<>();
    HashMap<String,Marker> serviceMarkerHashMap = new HashMap<>();
    HashMap<String,Marker> religionMarkerHashMap = new HashMap<>();
    private String url ="http://140.134.48.76/USR/API/API/Default/APPGetData?name=point&token=2EV7tVz0Pv6bLgB/aXRURg==";
    private Button switchLayerBtn;
    Set<String> checkedLayerSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_tracker);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pref = getSharedPreferences("userData",MODE_PRIVATE);
        teamID = pref.getString("teamID","000000");
        userID = pref.getString("userID","null");

        //預設popupwin裡的checkbox
        checkedLayerSet.add("history");

        pref.edit().putStringSet("checkedLayer",checkedLayerSet).apply();

        timer = new Timer();
        //固定檢查用戶坐標是否有移動
        timer.schedule(checkTask, 1000, 5000);

        mDatabase = FirebaseDatabase.getInstance();
        teamRef = mDatabase.getReference("team").child(teamID);
        usersRef = teamRef.child("userData");
        markersRef = teamRef.child("marker");

        switchLayerBtn = findViewById(R.id.layer_btn);
        switchLayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            popWindow("switchLayer");
            }
        });



    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(TeamTracker.this));

        messageHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
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
                        for(int i = 0 ; i <jsonArray.length();i++ ){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            xPoint = Double.parseDouble(jsonObject.get("PO_X").toString());
                            yPoint = Double.parseDouble(jsonObject.get("PO_Y").toString());
                            title = jsonObject.get("PO_TITLE").toString();
                            type = jsonObject.get("PO_TYPES").toString();
                            content = jsonObject.get("PO_CONTENT").toString();
                            id = jsonObject.get("PO_ID").toString();
                            Marker marker = null;

                            switch(Integer.parseInt(type)){
                                case 0://美食
                                    markerColor = BitmapDescriptorFactory.HUE_AZURE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    foodMarkerHashMap.put(id, marker);
                                    break;
                                case 1://購物
                                    markerColor = BitmapDescriptorFactory.HUE_BLUE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    shoppingMarkerHashMap.put(id, marker);
                                    break;
                                case 2://住宿
                                    markerColor = BitmapDescriptorFactory.HUE_CYAN;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    roomMarkerHashMap.put(id, marker);
                                    break;
                                case 3://歷史
                                    markerColor = BitmapDescriptorFactory.HUE_RED;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    historyMarkerHashMap.put(id, marker);
                                    break;
                                case 4://遊憩
                                    markerColor = BitmapDescriptorFactory.HUE_MAGENTA;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    playMarkerHashMap.put(id, marker);
                                    break;
                                case 5://交通
                                    markerColor = BitmapDescriptorFactory.HUE_ORANGE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    trafficMarkerHashMap.put(id, marker);
                                    break;
                                case 6://服務
                                    markerColor = BitmapDescriptorFactory.HUE_GREEN;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    serviceMarkerHashMap.put(id, marker);
                                    break;
                                case 7://宗教
                                    markerColor = BitmapDescriptorFactory.HUE_ROSE;
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng( yPoint, xPoint)).title(title).icon(BitmapDescriptorFactory.defaultMarker(markerColor)).snippet(content));
                                    marker.setVisible(false);
                                    religionMarkerHashMap.put(id, marker);
                                    break;
                            }
                            //需要設定什麼大小的時候會出現哪些marker
                            //float zoom = map.getCameraPosition().zoom;

                            //marker.showInfoWindow();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                };
            }
        };

        getDeviceLocation();
        getPointJson(url);

        //每次fireBase裡朋友資料更新時，更新本地朋友資料
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    String userName = data.child("userName").getValue(String.class);
                    String userIconPath = data.child("userIconPath").getValue(String.class);
                    String userID = data.getKey();

                    int iconPathID = getResources().getIdentifier(userIconPath, "drawable", getPackageName());
                    Bitmap userBitmap = new BitmapFactory().decodeResource(getResources(),iconPathID);
                    Double userLatitude = data.child("userLatitude").getValue(Double.class);
                    Double userLongitude = data.child("userLongitude").getValue(Double.class);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(userLatitude,userLongitude)).title(userName).icon(BitmapDescriptorFactory.fromBitmap(userBitmap)));
                    if(hashMapMarker.containsKey(userID)){
                        Marker delMarker = hashMapMarker.get(userID);
                        delMarker.remove();
                        hashMapMarker.remove(userID);
                    }
                    hashMapMarker.put(userID,marker);
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
                if (snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()){
                        String markContext = data.child("markContext").getValue(String.class);
                        //String userIconPath = data.child("userIconPath").getValue(String.class);
                        Double markLatitude = data.child("markLatitude").getValue(Double.class);
                        Double markLongitude = data.child("markLongitude").getValue(Double.class);

                        mMap.addMarker(new MarkerOptions().position(new LatLng(markLatitude,markLongitude)).title(markContext));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LOCATION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                String returnString = data.getStringExtra("markContext");
                mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude())).title(returnString));

            }
        }
    }

    //獲取使用者裝置現在的位置
    private void getDeviceLocation(){
        //這裡會有先載入地圖了再拋出permittion的bug

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
            location.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Map<String, Object> userLocations = new HashMap<>();
                DatabaseReference myRef = usersRef.child(userID);

                mCurrentLocation = (Location) location;
                //檢查user有沒有移動
                userLocations.put("userLatitude",mCurrentLocation.getLatitude());
                userLocations.put("userLongitude",mCurrentLocation.getLongitude());

                myRef.updateChildren(userLocations);

                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),20f);
            }
        });

    }

    private void checkLocationChange(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
        final float preLatitude = pref.getFloat("userLatitude",0);
        final float preLongitude = pref.getFloat("userLongitude",0);

        location.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mCurrentLocation = (Location) location;
                if(preLatitude != (float)mCurrentLocation.getLatitude() || preLongitude != (float)mCurrentLocation.getLongitude()){
                    Map<String, Object> userLocations = new HashMap<>();
                    //Log.d("seeIfSameLocation","notSame");
                    //Log.d("seeIfSameLocation",preLatitude + " " + preLongitude + " " + (float)mCurrentLocation.getLatitude() + " " + (float)mCurrentLocation.getLongitude());
                    userLocations.put("userLatitude",mCurrentLocation.getLatitude());
                    userLocations.put("userLongitude",mCurrentLocation.getLongitude());
                    usersRef.child(userID).updateChildren(userLocations);
                }
            }
        });
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

    public void popLocationInfo(View view) {
        popWindow("locationInfo");
    }

    public void popPersonInfo(View view) {
        popWindow("personInfo");
    }

    public void popWindow(String popWinName) {
        if(popWinName.equals("locationInfo")){
            LocationInfoPopUpWin locationInfoPopWin = new LocationInfoPopUpWin(this, R.layout.location_info_pop_win, mMap);
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
        } else if (popWinName.equals("personInfo")){
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

        } else if (popWinName.equals("switchLayer")){

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

        }

    }

    public void exitTeam(View view) {
        pref.edit().putBoolean("inTeam",false).commit();
        //這裡要去firebase刪掉相關用戶的資料，現在還沒實作
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void addLocation(View view) {
        pref.edit().putFloat("Latitude",(float)mCurrentLocation.getLatitude()).putFloat("Longitude",(float)mCurrentLocation.getLongitude()).commit();
        params = getWindow().getAttributes();
        params.alpha=1f;
        getWindow().setAttributes(params);
        Intent intent = new Intent(this, CreateNewMarker.class);
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
