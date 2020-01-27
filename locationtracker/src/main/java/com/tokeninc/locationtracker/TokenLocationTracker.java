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
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class TokenLocationTracker {

    public static String MIN_MILLIS_TIME_FOR_UPDATE = "min_millis_time_for_update";
    public static String MIN_METER_DISTANCE_FOR_UPDATE = "min_meter_distance_for_update";
    public static String PREFERRED_LOCATION_TRACKER = "preferred_location_tracker";
    private long minMillisTimeForUpdate = 1000L * 60; //A minute
    private int minMeterDistanceForUpdate = 50; //50 meters
    private String preferredLocationTracker = ForegroundLocationTracker.FUSED_PROVIDER;
    private static TokenLocationTracker instance;
    private ForegroundLocationTracker tracker;
    private ServiceConnection locationConnection;
    private Intent startLocationIntent;
    private String className;

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
        this.minMeterDistanceForUpdate = minDistance;
        this.minMillisTimeForUpdate = minPeriodInMillis;
        this.preferredLocationTracker = preferredNetwork;
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
        this.minMillisTimeForUpdate = minPeriodInMillis;
        this.preferredLocationTracker = preferredNetwork;
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
        this.minMeterDistanceForUpdate = minDistance;
        this.minMillisTimeForUpdate = minPeriodInMillis;
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
        this.minMeterDistanceForUpdate = minDistance;
        this.preferredLocationTracker = preferredNetwork;
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
        this.minMillisTimeForUpdate = minPeriodInMillis;
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
        this.preferredLocationTracker = preferredNetwork;
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
        this.minMeterDistanceForUpdate = minDistance;
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

    private void startLocationTrackingSystem(final WeakReference<? extends AppCompatActivity> weakReference,
                                             final LocationInformationCallback callback){
        Bundle bundle = new Bundle();
        if(locationConnection != null){
            //Do not start & bind multiple times
            return;
        }
        if(preferredLocationTracker != null && (preferredLocationTracker.equals(LocationManager.GPS_PROVIDER) ||
                preferredLocationTracker.equals(LocationManager.NETWORK_PROVIDER) ||
                preferredLocationTracker.equals(LocationManager.PASSIVE_PROVIDER))){
            className = weakReference.get().getClass().getName();
            bundle.putInt(MIN_METER_DISTANCE_FOR_UPDATE,this.minMeterDistanceForUpdate);
            bundle.putString(PREFERRED_LOCATION_TRACKER,this.preferredLocationTracker);
            bundle.putLong(MIN_MILLIS_TIME_FOR_UPDATE,this.minMillisTimeForUpdate);
            startLocationIntent = new Intent(weakReference.get(),ForegroundLocationTracker.class);
            startLocationIntent.setAction(Intent.ACTION_RUN);
            startLocationIntent.putExtras(bundle);
            weakReference.get().startService(startLocationIntent);
            locationConnection = new ServiceConnection() {
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
            weakReference.get().bindService(startLocationIntent, locationConnection, Context.BIND_AUTO_CREATE);
        }
        else{
            throw new IllegalArgumentException("LocationTrackerParameter is wrong,use LocationManager provided trackers instead");
        }
    }

    public void stopLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference){
        if(className.equals(weakReference.get().getClass().getName())){
            weakReference.get().unbindService(locationConnection);
            weakReference.get().stopService(startLocationIntent);
            instance = null;
        }
        else{
            throw new IllegalStateException("Location tracker should be closed from same activity called from");
        }

    }
}
