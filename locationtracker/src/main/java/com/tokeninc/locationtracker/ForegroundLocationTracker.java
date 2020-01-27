package com.tokeninc.locationtracker;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.NotActiveException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.tokeninc.locationtracker.TokenLocationTracker.MIN_METER_DISTANCE_FOR_UPDATE;
import static com.tokeninc.locationtracker.TokenLocationTracker.MIN_MILLIS_TIME_FOR_UPDATE;
import static com.tokeninc.locationtracker.TokenLocationTracker.PREFERRED_LOCATION_TRACKER;

public class ForegroundLocationTracker extends Service implements LocationListener {


    private final String notificationChannelStringId = "token_location_tracker";
    private final String notificationChannelName = "Token Location Tracker";
    public static final String FUSED_PROVIDER = "fused";
    private final int notificationNotifyId = 1502;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private Bundle params;
    private LocationManager locationManager;
    private final IBinder binder = new TokenLocationTrackerBinder();
    private long minMillisTimeForUpdate;
    private int minMeterDistanceForUpdate;
    private String preferredLocationTracker = FUSED_PROVIDER;
    private LocationInformationCallback callback;
    private Location location;
    private boolean isGpsEnabled = false,isNetworkEnabled = false,isPassiveEnabled = false;


    class TokenLocationTrackerBinder extends Binder{
        ForegroundLocationTracker getInstance(
                Bundle params, LocationInformationCallback callback){
            ForegroundLocationTracker.this.params = params;
            minMillisTimeForUpdate = params.getLong(MIN_MILLIS_TIME_FOR_UPDATE,minMillisTimeForUpdate);
            minMeterDistanceForUpdate = params.getInt(MIN_METER_DISTANCE_FOR_UPDATE,minMeterDistanceForUpdate);
            preferredLocationTracker = params.getString(PREFERRED_LOCATION_TRACKER,preferredLocationTracker);
            ForegroundLocationTracker.this.callback = callback;
            startLocationManager();
            return ForegroundLocationTracker.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        params = intent.getExtras();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        if(intent.getAction() != null && intent.getAction().equals(Manifest.permission.ACCESS_FINE_LOCATION)){
            startLocationManager();
        }
        else if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_RUN)){
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
                callback.onError(new IllegalStateException(
                        "There are no location service opened,either all of them disabled or not running properly"));
            }
            else if(locationManager == null){
                callback.onError(new IllegalStateException(
                        "Location Manager not properly set or null"));
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
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if(callback != null){
            callback.onLocationUpdate(location);
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
                break;
            case "network":
                isNetworkEnabled = true;
                break;
            case "passive":
                isPassiveEnabled = true;
                break;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        switch (provider){
            case "gps":
                isGpsEnabled = false;
                if(callback != null){
                    callback.onError(new NotActiveException("GPS Provider Disabled"));
                }
                break;
            case "network":
                isNetworkEnabled = false;
                if(callback != null){
                    callback.onError(new NotActiveException("Network Provider Disabled"));
                }
                break;
            case "passive":
                isPassiveEnabled = false;
                if(callback != null){
                    callback.onError(new NotActiveException("Passive Provider Disabled"));
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
