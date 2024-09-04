package com.android.gids;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class LocationService {

    private static double latitude;
    private static double longitude;
    private static boolean isLocationAvailable = false;

    private static LocationManager locationManager;
    private static LocationListener locationListener;

    public static void requestLocation(Context context) {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        if (latitude != 0.0 && longitude != 0.0) {
                            isLocationAvailable = true;
                        }
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // Request updates from the GPS provider
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener, Looper.getMainLooper());
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

    public static void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
