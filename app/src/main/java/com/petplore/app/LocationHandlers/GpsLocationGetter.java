package com.petplore.app.LocationHandlers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.petplore.app.MainActivity;

public class GpsLocationGetter {

    Activity activity;
    //    FusedLocationProviderClient fusedLocationClient;
    Location currentLocation = null;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    public GpsLocationGetter() {
    }

    public GpsLocationGetter(Activity activity) {
        this.activity = activity;
//        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }


    public void getGpsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;

//              TODO IMPORTANT check if permission is granted
            }

        }
    }

    public void getAllLocationAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (!checkLocationIsOn()) {
                    requestToTurnOnLocation();
                }
            } else {
                // TODO replace with alert
                Toast.makeText(activity, "(REPLACE WITH ALERT)To use this you need to grant gps premisson", Toast.LENGTH_SHORT).show();
                getGpsPermission();
            }
        } else if (!checkLocationIsOn()) {
            requestToTurnOnLocation();
        }
    }

    public boolean isGpsAccessible() {
        boolean isAccessible = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                isAccessible = false;
            }
        }

        if (!checkLocationIsOn()) {
            isAccessible = false;
        }

        return isAccessible;

    }

    public boolean checkLocationIsOn() {
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);


        boolean gps_enabled = false;

        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            return false;
        } else {
            return true;
        }


    }

    public void requestToTurnOnLocation() {
        new AlertDialog.Builder(activity)
                .setMessage("Enable GPS")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }


    public Location getCurrentLocation() {
        return currentLocation;
    }

    //TODO TEST
//    LocationManager locationManager;
////
////    public void newGpsLocationGetterMethod(final Runnable onLocationChangeFunction) {
////
////        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
////
////        // Define a listener that responds to location updates
////        LocationListener locationListener = new LocationListener() {
////            public void onLocationChanged(Location location) {
////
////                currentLocation = location;
////                Toast.makeText(activity, "Gps locked", Toast.LENGTH_SHORT).show();
////
////                onLocationChangeFunction.run();
////            }
////
////            public void onStatusChanged(String provider, int status, Bundle extras) {
////            }
////
////            public void onProviderEnabled(String provider) {
////            }
////
////            public void onProviderDisabled(String provider) {
////                Toast.makeText(activity, "location can not be accessed", Toast.LENGTH_SHORT);
////
////                getAllLocationAccess();
////            }
////        };
////
////        // Register the listener with the Location Manager to receive location updates
////
////        // check permissions
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                // TODO: Consider calling
////                //    Activity#requestPermissions
////                // here to request the missing permissions, and then overriding
////                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                //                                          int[] grantResults)
////                // to handle the case where the user grants the permission. See the documentation
////                // for Activity#requestPermissions for more details.
////            }
////        }
////
////        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
////
////    }
    //TODO TEST

    // TODO NEW LOCATION GETTER
    public void getLocationWithPermission (Runnable runAfterLocationSet){
        if(isGpsAccessible()) {
            getCurrentLocationNew(runAfterLocationSet);
        } else {
            getAllLocationAccess();
        }
    }


    public void getCurrentLocationNew(final Runnable runAfterLocationSetRunnable) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            getAllLocationAccess();
            return;
        }
        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(this);

                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int lastLocationIndex = locationResult.getLocations().size() - 1;
                    currentLocation = locationResult.getLocations().get(lastLocationIndex);
                    Toast.makeText(activity, "Gps locked", Toast.LENGTH_SHORT).show();
                    runAfterLocationSetRunnable.run();
                }
            }
        }, Looper.getMainLooper());

    }
    // TODO NEW LOCATION GETTER
}
