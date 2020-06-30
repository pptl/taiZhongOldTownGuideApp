package com.example.taizhongoldtownguideapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class whereIsMyFriend extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mCurrentLocation;

    FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_is_my_friend);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                Toast.makeText(whereIsMyFriend.this, "marker clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });




        getDeviceLocation();
    }

    private void getDeviceLocation(){
        //這裡會有先載入地圖了再拋出permittion的bug

        mFusedLocationProviderClient = LocationServices.
        getFusedLocationProviderClient(this);
        final Task<Location> location = mFusedLocationProviderClient.getLastLocation();

        location.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //Toast.makeText(whereIsMyFriend.this, "nonoononon", Toast.LENGTH_SHORT).show();
                //fake sarah and manda location
                Bitmap sarahBitmap = new BitmapFactory().decodeResource(getResources(),R.drawable.sarah);
                Bitmap mandaBitmap = new BitmapFactory().decodeResource(getResources(),R.drawable.manda);
                mCurrentLocation = (Location) location;
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude()+0.001,location.getLongitude()+0.001)).title("Sarah").icon(BitmapDescriptorFactory.fromBitmap(sarahBitmap)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude()+0.001,location.getLongitude())).title("Manda").icon(BitmapDescriptorFactory.fromBitmap(mandaBitmap)));
                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),20f);
            }
        });

    }

    //用來標記你朋友的位置
    private void getMyFriendLocation() {

    }
    private void moveCamera(LatLng latLng, float zoom){
        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
}
