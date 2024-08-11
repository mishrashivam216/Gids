package com.android.gids;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService {

    private static double latitude;
    private static double longitude;
    private static boolean isLocationAvailable = false;

    public static void requestLocation(Context context) {
        try {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000); // 10 seconds
            locationRequest.setFastestInterval(5000); // 5 seconds

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                            isLocationAvailable = true;
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
//                    Toast.makeText(context, "Lat: " + latitude + " Long: " + longitude, Toast.LENGTH_LONG).show();
                    }
                }
            };

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLat() {
        try {
            return isLocationAvailable ? String.valueOf(latitude) : "0.0";
        } catch (Exception e) {
            return "0.0";
        }
    }

    public static String getLong() {
        try {
            return isLocationAvailable ? String.valueOf(longitude) : "0.0";
        } catch (Exception e) {
            return "0.0";

        }
    }
}
