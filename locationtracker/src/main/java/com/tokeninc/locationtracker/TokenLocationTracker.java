package com.tokeninc.locationtracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class TokenLocationTracker {

    public static String MIN_MILLIS_TIME_FOR_UPDATE = "min_millis_time_for_update";
    public static String MIN_METER_DISTANCE_FOR_UPDATE = "min_meter_distance_for_update";
    public static String PREFERRED_LOCATION_TRACKER = "preferred_location_tracker";
    private long minMillisTimeForUpdate = 1000L; //Default values
    private int minMeterDistanceForUpdate = 10; //Default values
    private NetworkStatus preferredLocationTracker; //Default = NetworkStatus.GPS
    private static TokenLocationTracker instance;
    private ServiceConnection locationConnection;
    private Intent startLocationIntent;
    private ForegroundLocationTracker tracker;

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
                                      long minPeriodInMillis,@NonNull NetworkStatus preferredNetwork,
                                      int minDistance,
                                      final LocationInformationCallback callback){
        this.minMeterDistanceForUpdate = minDistance;
        this.minMillisTimeForUpdate = minPeriodInMillis;
        this.preferredLocationTracker = preferredNetwork;
        this.preferredLocationTracker.setStatus(true);
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
                                      long minPeriodInMillis,@NonNull NetworkStatus preferredNetwork,
                                      final LocationInformationCallback callback){
        this.minMillisTimeForUpdate = minPeriodInMillis;
        this.preferredLocationTracker = preferredNetwork;
        this.preferredLocationTracker.setStatus(true);
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
        this.preferredLocationTracker = NetworkStatus.GPS;
        this.preferredLocationTracker.setStatus(true);
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
                                      int minDistance,@NonNull NetworkStatus preferredNetwork,
                                      final LocationInformationCallback callback){
        this.minMeterDistanceForUpdate = minDistance;
        this.preferredLocationTracker = preferredNetwork;
        this.preferredLocationTracker.setStatus(true);
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
        this.preferredLocationTracker = NetworkStatus.GPS;
        this.preferredLocationTracker.setStatus(true);
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param preferredNetwork Changing preferred network for location tracking
     * @param callback Location Callback Interface
     */

    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      @NonNull NetworkStatus preferredNetwork,
                                      final LocationInformationCallback callback){
        this.preferredLocationTracker = preferredNetwork;
        this.preferredLocationTracker.setStatus(true);
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
        this.preferredLocationTracker = NetworkStatus.GPS;
        this.preferredLocationTracker.setStatus(true);
        startLocationTrackingSystem(weakReference, callback);
    }

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param callback Location Callback Interface
     */

    public void startLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference,
                                      final LocationInformationCallback callback){
        this.preferredLocationTracker = NetworkStatus.GPS;
        this.preferredLocationTracker.setStatus(true);
        startLocationTrackingSystem(weakReference, callback);
    }

    private void startLocationTrackingSystem(final WeakReference<? extends AppCompatActivity> weakReference,
                                             final LocationInformationCallback callback){
        Bundle bundle = new Bundle();
        bundle.putInt(MIN_METER_DISTANCE_FOR_UPDATE,this.minMeterDistanceForUpdate);
        bundle.putSerializable(MIN_MILLIS_TIME_FOR_UPDATE,this.preferredLocationTracker);
        bundle.putLong(MIN_MILLIS_TIME_FOR_UPDATE,this.minMillisTimeForUpdate);
        startLocationIntent = new Intent(weakReference.get(),ForegroundLocationTracker.class);
        startLocationIntent.putExtras(bundle);
        weakReference.get().startService(startLocationIntent);
        locationConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ForegroundLocationTracker.TokenLocationTrackerBinder mLocalBinder =
                        (ForegroundLocationTracker.TokenLocationTrackerBinder) service;
                tracker = mLocalBinder.getInstance(bundle,callback,weakReference);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                tracker = null;
            }
        };
        weakReference.get().bindService(startLocationIntent, locationConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *
     * @param weakReference Activity reference for removing bind on running activity
     */

    public void stopLocationTracking(final WeakReference<? extends AppCompatActivity> weakReference){
        weakReference.get().unbindService(locationConnection);
        weakReference.get().stopService(startLocationIntent);
    }
}
