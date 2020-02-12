package com.tokeninc.locationtracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.tokeninc.foregroundImplementation.IForegroundLocationObserver;
import com.tokeninc.foregroundImplementation.ITokenForegroundLocationTracker;
import com.tokeninc.foregroundImplementation.MyLocation;


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
    private @Nullable IForegroundLocationObserver callback;
    private Location location;
    private boolean isGpsEnabled = false,isNetworkEnabled = false,isPassiveEnabled = false;


    private final ITokenForegroundLocationTracker.Stub tracker = new ITokenForegroundLocationTracker.Stub() {
        @Override
        public void registerCallback(IForegroundLocationObserver callback)  {
            ForegroundLocationTracker.this.callback = callback;
            if(location != null){
                try{
                    callback.onLocationUpdate(new MyLocation(location.getLatitude(),location.getLongitude(),location.getAltitude(),
                            location.getSpeed(),location.getBearing()));
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void unRegisterCallback(){
            ForegroundLocationTracker.this.callback = null;
        }
    };

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
        return tracker;
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
                if(callback != null){
                    try{
                        callback.onError(1);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
            }
            else if(locationManager == null){
                if(callback != null){
                    try{
                        callback.onError(2);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
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
        if(callback != null){
            try{
                callback.onLocationUpdate(new MyLocation(location.getLatitude(),location.getLongitude(),location.getAltitude(),
                        location.getSpeed(),location.getBearing()));
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
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
                if(callback != null){
                    try{
                        callback.onError(6);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
                break;
            case "network":
                isNetworkEnabled = true;
                if(callback != null){
                    try{
                        callback.onError(7);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
                break;
            case "passive":
                isPassiveEnabled = true;
                if(callback != null){
                    try{
                        callback.onError(8);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        switch (provider){
            case "gps":
                isGpsEnabled = false;
                if(callback != null){
                    try{
                        callback.onError(3);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
                break;
            case "network":
                isNetworkEnabled = false;
                if(callback != null){
                    try{
                        callback.onError(4);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
                break;
            case "passive":
                isPassiveEnabled = false;
                if(callback != null){
                    try{
                        callback.onError(5);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
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
