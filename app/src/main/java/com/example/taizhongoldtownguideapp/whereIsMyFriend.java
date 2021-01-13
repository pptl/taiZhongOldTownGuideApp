package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;


public class whereIsMyFriend extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mCurrentLocation;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private Button locateBtn;
    private Button personBtn;
    private WindowManager.LayoutParams params;
    private String teamID;
    private String userID;
    private String userIconPath;
    private FirebaseDatabase mDatabase;
    private DatabaseReference teamRef;
    private DatabaseReference usersRef;
    private DatabaseReference markersRef;
    private Timer timer;
    private SharedPreferences pref;
    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;
    HashMap<String,Marker> hashMapMarker = new HashMap<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_is_my_friend);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pref = getSharedPreferences("userData",MODE_PRIVATE);
        teamID = pref.getString("teamID","000000");
        userID = pref.getString("userID","null");
        timer = new Timer();

        mDatabase = FirebaseDatabase.getInstance();
        teamRef = mDatabase.getReference("team").child(teamID);
        usersRef = teamRef.child("userData");
        markersRef = teamRef.child("marker");

    }

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

        getDeviceLocation();


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("seeChildrenCount",snapshot.getChildrenCount()+" sdsdsd");
                for (DataSnapshot data : snapshot.getChildren()){
                    String userName = data.child("userName").getValue(String.class);
                    String userIconPath = data.child("userIconPath").getValue(String.class);
                    String userID = data.getKey();

                    int iconPathID = getResources().getIdentifier(userIconPath, "drawable", getPackageName());
                    Bitmap userBitmap = new BitmapFactory().decodeResource(getResources(),iconPathID);
                    Double userLatitude = data.child("userLatitude").getValue(Double.class);
                    Double userLongitude = data.child("userLongitude").getValue(Double.class);

                    //Log.d("seelocation",userName + " " + userLatitude + " " + userLongitude);

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

        markersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()){
                        String markContext = data.child("markContext").getValue(String.class);
                        String userIconPath = data.child("userIconPath").getValue(String.class);
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


        //addMarker
        timer.schedule(checkTask, 1000, 5000);




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
                //這一段是為了檢查user有沒有移動
                float userLatitude = (float)mCurrentLocation.getLatitude();
                float userLongitude = (float)mCurrentLocation.getLongitude();
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
                    Log.d("seeIfSameLocation","notSame");
                    Log.d("seeIfSameLocation",preLatitude + " " + preLongitude + " " + (float)mCurrentLocation.getLatitude() + " " + (float)mCurrentLocation.getLongitude());
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
            //Log.d("seeTimer","ffffff");
            checkLocationChange();

        }
    };


    //用來標記你朋友的位置
    private void moveCamera(LatLng latLng, float zoom){
        //mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    public void popLocationInfo(View view) {
        popWindow("locationInfo");
        Log.d("btn","popLocation On click");
    }

    public void popPersonInfo(View view) {
        popWindow("personInfo");
        Log.d("btn","popPerson On click");
    }

    public void popWindow(String popWinName) {
        if(popWinName.equals("locationInfo")){
            locationInfoPopWin locationInfoPopWin = new locationInfoPopWin(this,this, mMap);
            //设置Popupwindow显示位置（从底部弹出）
            locationInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            //当弹出Popupwindow时，背景变半透明
            params.alpha=0.7f;
            getWindow().setAttributes(params);
            //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
            locationInfoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha=1f;
                    getWindow().setAttributes(params);
                }
            });
        }
        else{
            personInfoPopWin personInfoPopWin = new personInfoPopWin(this, this, mMap);
            //设置Popupwindow显示位置（从底部弹出）
            personInfoPopWin.showAtLocation(findViewById(R.id.map), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            params = getWindow().getAttributes();
            //当弹出Popupwindow时，背景变半透明
            params.alpha=0.7f;
            getWindow().setAttributes(params);
            //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
           personInfoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    params = getWindow().getAttributes();
                    params.alpha=1f;
                    getWindow().setAttributes(params);
                }
            });

        }


    }

    public void exitTeam(View view) {
        pref.edit().putBoolean("inTeam",false).commit();
        //這裡要去firebase刪資料

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void addLocation(View view) {
        pref.edit().putFloat("Latitude",(float)mCurrentLocation.getLatitude()).putFloat("Longitude",(float)mCurrentLocation.getLongitude()).commit();
        //closePopOutWin
        params = getWindow().getAttributes();
        params.alpha=1f;
        getWindow().setAttributes(params);
        Intent intent = new Intent(this,addLocation.class);
        startActivityForResult(intent,ADD_LOCATION_ACTIVITY_REQUEST_CODE);
    }


}
