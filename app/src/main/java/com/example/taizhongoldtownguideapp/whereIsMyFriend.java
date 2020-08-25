package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
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


public class whereIsMyFriend extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mCurrentLocation;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private Button locateBtn;
    private Button personBtn;
    private WindowManager.LayoutParams params;
    private String teamID;
    private String userID;
    private String userIconPath;
    private Timer timer;
    private static final int ADD_LOCATION_ACTIVITY_REQUEST_CODE = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_is_my_friend);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        teamID = pref.getString("teamID","000000");
        userID = pref.getString("userID","RYPNZsgAFXpIb6PYYHlz");
        timer = new Timer();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //moveCamera(new LatLng(-34, 151), 15f);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Toast.makeText(whereIsMyFriend.this, "marker clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        getDeviceLocation();
        //addMarker
        timer.schedule(checkTask, 1000, 20000);
        timer.schedule(renewTask, 1000, 20000);

        CollectionReference teamMemberCollectionRef = db.collection("teamID").document(teamID).collection("userData");
        CollectionReference markCollectionRef = db.collection("teamID").document(teamID).collection("mark");




        markCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String markContext = document.getData().get("markContext").toString();
                        Double markLatitude = (Double) document.getData().get("markLatitude");
                        Double markLongitude = (Double) document.getData().get("markLongitude");
                        //document.getData().get("userName");
                        //document.getData().get("userName");
                        //friendList.add(document.getId());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(markLatitude,markLongitude)).title(markContext));
                    }
                } else {
                    Log.d("firebaseMember", "Error getting documents: ", task.getException());
                }
            }
        });

        teamMemberCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("seeshunxu","setLocationmarker");


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userName = document.getData().get("userName").toString();
                        String userIconPath = document.getData().get("userIconPath").toString();
                        //Log.d("seedouble",document.getData().get("userLatitude").toString());
                        int iconPathID = getResources().getIdentifier(userIconPath, "drawable", getPackageName());
                        Bitmap userBitmap = new BitmapFactory().decodeResource(getResources(),iconPathID);
                        Double userLatitude = (Double) document.getData().get("userLatitude");
                        Double userLongitude = (Double) document.getData().get("userLongitude");
                        Log.d("seelocation",userName + " " + userLatitude + " " + userLongitude);
                        if(userLatitude.intValue() != 0 && userLongitude.intValue() != 0){
                            mMap.addMarker(new MarkerOptions().position(new LatLng(userLatitude,userLongitude)).title(userName).icon(BitmapDescriptorFactory.fromBitmap(userBitmap)));
                        }
                    }
                } else {
                    Log.d("firebaseMember", "Error getting documents: ", task.getException());
                }
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
                // Set text view with string
                //TextView textView = (TextView) findViewById(R.id.textView);
                //textView.setText(returnString);
            }
        }
    }
    private void getDeviceLocation(){
        //這裡會有先載入地圖了再拋出permittion的bug
        final SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        mFusedLocationProviderClient = LocationServices.
        getFusedLocationProviderClient(this);
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
            location.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("seeshunxu","getDEVLocation");
                Map<String, Object> userLocations = new HashMap<>();
                //Toast.makeText(whereIsMyFriend.this, "nonoononon", Toast.LENGTH_SHORT).show();
                //fake sarah and manda location
                //Bitmap sarahBitmap = new BitmapFactory().decodeResource(getResources(),R.drawable.sarah);
               // Bitmap mandaBitmap = new BitmapFactory().decodeResource(getResources(),R.drawable.manda);
                mCurrentLocation = (Location) location;
                userLocations.put("userLatitude",mCurrentLocation.getLatitude());
                userLocations.put("userLongitude",mCurrentLocation.getLongitude());
                pref.edit().putFloat("userLatitude",(float)mCurrentLocation.getLatitude()).putFloat("userLongitude",(float)mCurrentLocation.getLongitude()).commit();
                db.collection("teamID").document(teamID).collection("userData").document(userID).update(userLocations);

                //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude()+0.001,location.getLongitude()+0.001)).title("Sarah").icon(BitmapDescriptorFactory.fromBitmap(sarahBitmap)));
                //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude()+0.001,location.getLongitude())).title("Manda").icon(BitmapDescriptorFactory.fromBitmap(mandaBitmap)));
                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),20f);
            }
        });

    }
    private void checkLocationChange(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
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
                    db.collection("teamID").document(teamID).collection("userData").document(userID).update(userLocations);
                }
                else{
                    //Log.d("seeIfSameLocation","same");
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
    private TimerTask renewTask = new TimerTask() {
        @Override
        public void run() {

            Log.d("seeIfSameLocation","iamrenew");

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
            locationInfoPopWin locationInfoPopWin = new locationInfoPopWin(this,this);
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
        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        pref.edit().putBoolean("inTeam",false).commit();

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void addLocation(View view) {
        SharedPreferences pref = getSharedPreferences("userData",MODE_PRIVATE);
        pref.edit().putFloat("Latitude",(float)mCurrentLocation.getLatitude()).putFloat("Longitude",(float)mCurrentLocation.getLongitude()).commit();
        Intent intent = new Intent(this,addLocation.class);
        startActivityForResult(intent,ADD_LOCATION_ACTIVITY_REQUEST_CODE);
    }


}
