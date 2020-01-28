package com.tokeninc.locationtracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TokenLocationTracker {

    public static String MIN_MILLIS_TIME_FOR_UPDATE = "min_millis_time_for_update";
    public static String MIN_METER_DISTANCE_FOR_UPDATE = "min_meter_distance_for_update";
    public static String PREFERRED_LOCATION_TRACKER = "preferred_location_tracker";
    private long minMillisTimeForUpdate = 1000L * 60; //A minute
    private int minMeterDistanceForUpdate = 50; //50 meters
    private String preferredLocationTracker = ForegroundLocationTracker.FUSED_PROVIDER;
    private static TokenLocationTracker instance;
    private ForegroundLocationTracker tracker;
    private HashMap<String,ServiceConnection> locationConnection = new HashMap<>();
    private Intent startLocationIntent;
    private String className;
    private Bundle bundle;

    public static TokenLocationTracker getInstance() {
        if(instance == null){
            instance = new TokenLocationTracker();
        }
        return instance;
    }

    private TokenLocationTracker(){

    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param minPeriodInMillis Changing default min period in milliseconds
     * @param preferredNetwork Changing preferred network for location tracking
     * @param minDistance Changing minimum distance required to move
     * @param callback Location Callback Interface
     */
    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      long minPeriodInMillis,@NonNull String preferredNetwork,
                                      int minDistance,
                                      final LocationInformationCallback callback){
        if(bundle == null){
            this.minMeterDistanceForUpdate = minDistance;
            this.minMillisTimeForUpdate = minPeriodInMillis;
            this.preferredLocationTracker = preferredNetwork;
        }

        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param minPeriodInMillis Changing default min period in milliseconds
     * @param preferredNetwork Changing preferred network for location tracking
     * @param callback Location Callback Interface
     */

    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      long minPeriodInMillis,@NonNull String preferredNetwork,
                                      final LocationInformationCallback callback){
        if(bundle == null){
            this.minMillisTimeForUpdate = minPeriodInMillis;
            this.preferredLocationTracker = preferredNetwork;
        }
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param minPeriodInMillis Changing default min period in milliseconds
     * @param minDistance Changing minimum distance required to move
     * @param callback Location Callback Interface
     */
    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      long minPeriodInMillis,int minDistance,
                                      final LocationInformationCallback callback){
        if(bundle == null){
            this.minMeterDistanceForUpdate = minDistance;
            this.minMillisTimeForUpdate = minPeriodInMillis;
        }
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param preferredNetwork Changing preferred network for location tracking
     * @param minDistance Changing minimum distance required to move
     * @param callback Location Callback Interface
     */
    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      int minDistance,@NonNull String preferredNetwork,
                                      final LocationInformationCallback callback){
        if(bundle == null){
            this.minMeterDistanceForUpdate = minDistance;
            this.preferredLocationTracker = preferredNetwork;
        }
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param minPeriodInMillis Changing default min period in milliseconds
     * @param callback Location Callback Interface
     */
    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      long minPeriodInMillis,
                                      final LocationInformationCallback callback){
        if(bundle == null){
            this.minMillisTimeForUpdate = minPeriodInMillis;
        }
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param preferredNetwork Changing preferred network for location tracking
     * @param callback Location Callback Interface
     */

    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      @NonNull String preferredNetwork,
                                      final LocationInformationCallback callback){
        if(bundle == null){
            this.preferredLocationTracker = preferredNetwork;
        }
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param minDistance Changing minimum distance required to move
     * @param callback Location Callback Interface
     */

    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      int minDistance,
                                      final LocationInformationCallback callback){
        if(bundle == null){
            this.minMeterDistanceForUpdate = minDistance;
        }
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param callback Location Callback Interface
     */

    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      final LocationInformationCallback callback){
        startLocationTrackingSystem(weakReference, callback);
    }

    public void startLocationTrackingInBackground(@NonNull Context context,
                                                   @Nullable Data data){
        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build();
        PeriodicWorkRequest locationTracker;
        if(data == null){
            locationTracker = new PeriodicWorkRequest.Builder(BackgroundLocationTracker.class,
                            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                            .setConstraints(constraints).build();
        }
        else{
            locationTracker = new PeriodicWorkRequest.Builder(BackgroundLocationTracker.class,
                            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                            .setConstraints(constraints).setInputData(data).build();
        }

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("locationTracker",
                ExistingPeriodicWorkPolicy.KEEP , locationTracker);
    }

    public void stopLocationTrackingInBackground(@NonNull Context context){
        WorkManager.getInstance(context).cancelUniqueWork("locationTracker");
    }

    private void startLocationTrackingSystem(final WeakReference<? extends AppCompatActivity> weakReference,
                                             final LocationInformationCallback callback){
        if(bundle == null){
            bundle = new Bundle();
            bundle.putInt(MIN_METER_DISTANCE_FOR_UPDATE,this.minMeterDistanceForUpdate);
            bundle.putString(PREFERRED_LOCATION_TRACKER,this.preferredLocationTracker);
            bundle.putLong(MIN_MILLIS_TIME_FOR_UPDATE,this.minMillisTimeForUpdate);
        }
        if(preferredLocationTracker != null && (preferredLocationTracker.equals(LocationManager.GPS_PROVIDER) ||
                preferredLocationTracker.equals(LocationManager.NETWORK_PROVIDER) ||
                preferredLocationTracker.equals(LocationManager.PASSIVE_PROVIDER) ||
                preferredLocationTracker.equals(ForegroundLocationTracker.FUSED_PROVIDER))){
            startLocationIntent = new Intent(weakReference.get(),ForegroundLocationTracker.class);
            startLocationIntent.setAction(Intent.ACTION_RUN);
            startLocationIntent.putExtras(bundle);
            if(tracker != null){
                callback.onError(new IllegalAccessException("Service already running,you cannot change parameters at first launch"));
            }
            else{
                weakReference.get().startService(startLocationIntent);
            }
            ServiceConnection connection = locationConnection.get(weakReference.get().getClass().getName());
            if(connection == null){
                connection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        ForegroundLocationTracker.TokenLocationTrackerBinder mLocalBinder =
                                (ForegroundLocationTracker.TokenLocationTrackerBinder) service;
                        tracker = mLocalBinder.getInstance(bundle,callback);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        tracker = null;
                    }
                };
                weakReference.get().bindService(startLocationIntent, connection, Context.BIND_AUTO_CREATE);
                locationConnection.put(weakReference.get().getClass().getName(),connection);
            }

        }
        else{
            throw new IllegalArgumentException("LocationTrackerParameter is wrong,use LocationManager provided trackers instead");
        }
    }

    public void stopLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference){
        ServiceConnection connection = locationConnection.get(weakReference.get().getClass().getName());
        if(connection != null) {
            weakReference.get().unbindService(connection);
            locationConnection.remove(weakReference.get().getClass().getName());
            if(locationConnection.isEmpty()){
                weakReference.get().stopService(startLocationIntent);
                tracker = null;
                instance = null;
            }
        }
        else{
            throw new IllegalStateException("Location tracker should be closed from same activity called from");
        }

    }
}
