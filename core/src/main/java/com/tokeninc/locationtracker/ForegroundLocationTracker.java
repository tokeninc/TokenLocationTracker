package com.tokeninc.locationtracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


public class ForegroundLocationTracker extends Service implements LocationListener {


    private final String notificationChannelStringId = "token_location_tracker";
    private final String notificationChannelName = "Token MyLocation Tracker";
    private static String MIN_MILLIS_TIME_FOR_UPDATE = "min_millis_time_for_update";
    private static String MIN_METER_DISTANCE_FOR_UPDATE = "min_meter_distance_for_update";
    private static String PREFERRED_LOCATION_TRACKER = "preferred_location_tracker";
    private static final String FUSED_PROVIDER = "fused";
    private long minMillisTimeForUpdate = 1000L * 60; //A minute
    private int minMeterDistanceForUpdate = 50; //50 meters
    private final int notificationNotifyId = 1502;
    private @Nullable NotificationCompat.Builder notificationBuilder;
    private @Nullable NotificationManager notificationManager;
    private @Nullable Bundle params;
    private @Nullable LocationManager locationManager;
    private String preferredLocationTracker = FUSED_PROVIDER;
    private Location location;
    private boolean isGpsEnabled = false,isNetworkEnabled = false,isPassiveEnabled = false;


    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        params = intent.getExtras();
        if(intent.getAction() != null && intent.getAction().equals(Manifest.permission.ACCESS_FINE_LOCATION)){
            startLocationManager();
        }
        else if(intent.getAction() != null && intent.getAction().equals("com.tokeninc.locationtracker.REQUEST_LOCATION")){
            startLocationManager();
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        if(intent.getAction() != null && intent.getAction().equals(Manifest.permission.ACCESS_FINE_LOCATION)){
            startLocationManager();
        }
        else if(intent.getAction() != null && intent.getAction().equals("com.tokeninc.locationtracker.REQUEST_LOCATION")){
            startLocationManager();
        }
        return START_NOT_STICKY;
    }

    void startLocationManager(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(this,LocationPermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Manifest.permission.ACCESS_FINE_LOCATION);
            startActivity(intent);
        }
        else{
            locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            if(locationManager != null){
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                isPassiveEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
            }

            if (!isGpsEnabled && !isNetworkEnabled && !isPassiveEnabled) {
                sendBroadcast(1);
            }
            else if(locationManager == null){
                sendBroadcast(2);
            }
            else {
                if(!preferredLocationTracker.equals(FUSED_PROVIDER)){
                    locationManager.requestLocationUpdates(
                            preferredLocationTracker,
                            minMillisTimeForUpdate,
                            minMeterDistanceForUpdate, this);
                    location = locationManager
                            .getLastKnownLocation(preferredLocationTracker);
                    showNotification();
                }
                else{
                    if (isGpsEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                minMillisTimeForUpdate,
                                minMeterDistanceForUpdate, this);
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        showNotification();

                    }
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                minMillisTimeForUpdate,
                                minMeterDistanceForUpdate, this);
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        showNotification();

                    }
                    if(isPassiveEnabled){
                        locationManager.requestLocationUpdates(
                                LocationManager.PASSIVE_PROVIDER,
                                minMillisTimeForUpdate,
                                minMeterDistanceForUpdate, this);
                        location = locationManager
                                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                        showNotification();
                    }
                }
            }
        }
    }

    private void sendBroadcast(Location location){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("location",location);
        intent.putExtras(bundle);
        intent.setAction("com.tokeninc.locationtracker.FOREGROUND_LOCATION_UPDATE");
        getApplicationContext().sendBroadcast(intent);
    }

    private void sendBroadcast(int code){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("error",code);
        intent.putExtras(bundle);
        intent.setAction("com.tokeninc.locationtracker.FOREGROUND_LOCATION_INFO");
        getApplicationContext().sendBroadcast(intent);
    }

    void showNotification(){
        if(notificationManager == null){
            notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),notificationChannelStringId);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setSmallIcon(android.R.drawable.ic_menu_mylocation);
        notificationBuilder.setContentTitle("Location checking...");
        notificationBuilder.setProgress(100, 60, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(notificationChannelStringId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_LOW;
                notificationChannel = new NotificationChannel(notificationChannelStringId, notificationChannelName, importance);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        startForeground(notificationNotifyId,notificationBuilder.build());
    }

    private void hideNotification(){
        stopForeground(true);
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(notificationManager != null){
            notificationManager.cancel(100);
        }
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        sendBroadcast(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (provider){
            case "gps":
                break;
            case "network":
                break;
            case "passive":
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        switch (provider){
            case "gps":
                isGpsEnabled = true;
                sendBroadcast(6);
                break;
            case "network":
                isNetworkEnabled = true;
                sendBroadcast(7);
                break;
            case "passive":
                isPassiveEnabled = true;
                sendBroadcast(8);
                break;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        switch (provider){
            case "gps":
                isGpsEnabled = false;
                sendBroadcast(3);
                break;
            case "network":
                isNetworkEnabled = false;
                sendBroadcast(4);
                break;
            case "passive":
                isPassiveEnabled = false;
                sendBroadcast(5);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideNotification();
        isGpsEnabled = false;
        isNetworkEnabled = false;
        isPassiveEnabled = false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        hideNotification();
        isGpsEnabled = false;
        isNetworkEnabled = false;
        isPassiveEnabled = false;
    }
}
