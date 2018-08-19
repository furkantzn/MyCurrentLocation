package com.example.furkan.mycurrentlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    List<Address> addresses = null;//I defined adresses for user
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    MarkerOptions markerOptions = new MarkerOptions();
    Button mShowDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);//I have installed the Maps fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);//I defined map fragment inside activity

        mapFragment.getMapAsync(this);


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);//I control the internet in this section.



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //When the map is ready, the following actions will take place.
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //If the user declines the incoming permissions position the marker to the following location and show the icon as hourglass.
        markerOptions.position(new LatLng(52.3545653, 4.7585396))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.clock_mini));
        Marker markerMapready = mMap.addMarker(markerOptions);
        markerMapready.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.3545653, 4.7585396), 7));
        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //The following actions will take place when the location changes
                mMap.clear();//Delete old map when each location changes.
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());//Find location of user

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        System.out.println("adress info" + addresses.get(0).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                markerOptions.position(userLocation)//Show marker on the location of user
                        .title(addresses.get(0).getAddressLine(0))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker));
                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //Performs the following operations when GPS is turned off.
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this, AlertDialog.THEME_TRADITIONAL);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("Location services disabled");
                alertDialog.setMessage("Turn on location access");
                alertDialog.setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        });
                alertDialog.setNegativeButton("Go to Settings",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                alertDialog.show();
            }
        };

        // Add a marker in Sydney and move the camera

        if (Build.VERSION.SDK_INT >= 23) {//If API > 23 want permission from user
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 20, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//Find the last location of user

                if (lastLocation != null) {// Get the user's last location information if the end position is not null
                    LatLng userLastlocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                        if (addresses != null && addresses.size() > 0) {
                            System.out.println("adress info" + addresses.get(0).toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    markerOptions.position(userLastlocation)//Show marker on last location of user
                            .title(addresses.get(0).getAddressLine(0))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker));
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastlocation, 16));
                } else {
                    markerOptions.position(new LatLng(52.3545653, 4.7585396))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.clock_mini));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.3545653, 4.7585396), 7));

                }


            }
        } else {

            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0,20, locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng userLastlocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        System.out.println("adress info" + addresses.get(0).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                markerOptions.position(userLastlocation)
                        .title(addresses.get(0).getAddressLine(0))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker));
                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastlocation, 16));
            } else {
                markerOptions.position(new LatLng(52.3545653, 4.7585396))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.clock_mini));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.3545653, 4.7585396), 7));

            }


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //After responding to the user's permission requests, the following actions will occur
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //If you have access to the location, perform the following operations.
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 20, locationListener);

                if (lastLocation != null) {
                    LatLng userLastlocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                        if (addresses != null && addresses.size() > 0) {
                            System.out.println("adress info" + addresses.get(0).toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    markerOptions.position(userLastlocation)
                            .title(addresses.get(0).getAddressLine(0))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker));
                    Marker marker = mMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastlocation, 16));
                } else {
                    markerOptions.position(new LatLng(52.3545653, 4.7585396))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.clock_mini));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.3545653, 4.7585396), 7));
                }


            } else {
                //perform the following actions if the user has declined the permissions.
                final boolean isGpsEnabled;
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                markerOptions.position(new LatLng(52.3545653, 4.7585396))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.clock_mini));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.3545653, 4.7585396), 7));
                if (isGpsEnabled) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this, AlertDialog.THEME_TRADITIONAL);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Location services disabled");
                    alertDialog.setMessage("Turn on location access ");
                    alertDialog.setPositiveButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    alertDialog.setNegativeButton("Try again\n",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    //Toast.makeText(context, "Inthernet connection is available", Toast.LENGTH_LONG).show();
                                   /*   new CountDownTimer(6000, 1000) {

                                        public void onTick(long millisUntilFinished) {

                                        }

                                        public void onFinish() {

                                        }
                                    }.start();*/
                                }
                            });

                    alertDialog.show();
                }
            }
        }


    }



}