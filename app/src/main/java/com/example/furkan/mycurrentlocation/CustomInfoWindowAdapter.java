package com.example.furkan.mycurrentlocation;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

//Java class written for the popup on the marker
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowAdapter(Context ctx){
        context = ctx;
    }



    @Override
    public View getInfoWindow(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow, null);

        TextView name_tv = view.findViewById(R.id.marker_header);//Pop-up header name
        TextView adress_tv = view.findViewById(R.id.marker_adress);//Address part in Pop-up

        name_tv.setText("Your Location:");
        adress_tv.setText(marker.getTitle());



        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}