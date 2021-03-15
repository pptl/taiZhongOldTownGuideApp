package com.example.taizhongoldtownguideapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mWindow = null;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView titleTextView = view.findViewById(R.id.info_window_title);

        if(!titleTextView.equals("")){
            titleTextView.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView snippetTextView = view.findViewById(R.id.info_window_content);

        if(!snippetTextView.equals("")){
            snippetTextView.setText(snippet);
        }


    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }
}
