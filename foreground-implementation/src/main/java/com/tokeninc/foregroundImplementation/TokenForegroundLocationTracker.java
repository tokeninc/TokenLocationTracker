package com.tokeninc.foregroundImplementation;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

public class TokenForegroundLocationTracker implements LifecycleObserver {

    private Bundle bundle;
    private long minMillisTimeForUpdate = 1000L * 60; //A minute
    private int minMeterDistanceForUpdate = 50; //50 meters
    private String preferredLocationTracker = FUSED_PROVIDER;
    private static String MIN_MILLIS_TIME_FOR_UPDATE = "min_millis_time_for_update";
    private static String MIN_METER_DISTANCE_FOR_UPDATE = "min_meter_distance_for_update";
    private static String PREFERRED_LOCATION_TRACKER = "preferred_location_tracker";
    private static final String FUSED_PROVIDER = "fused";
    private ForegroundLocationObserver locationTracker;
    private WeakReference<? extends AppCompatActivity> weakReference;
    private Intent startLocationIntent;

    /**
     *
     * @param weakReference Activity reference for binding service to running activity
     * @param minPeriodInMillis Changing default min period in milliseconds
     * @param preferredNetwork Changing preferred network for location tracking
     * @param minDistance Changing minimum distance required to move
     * @param callback Location Callback Interface
     */
    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          long minPeriodInMillis, @NonNull String preferredNetwork,
                                          int minDistance,
                                          final ForegroundLocationObserver callback){
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

    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          long minPeriodInMillis, @NonNull String preferredNetwork,
                                          final ForegroundLocationObserver callback){
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
    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          long minPeriodInMillis, int minDistance,
                                          final ForegroundLocationObserver callback){
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
    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          int minDistance, @NonNull String preferredNetwork,
                                          final ForegroundLocationObserver callback){
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
    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          long minPeriodInMillis,
                                          final ForegroundLocationObserver callback){
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

    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          @NonNull String preferredNetwork,
                                          final ForegroundLocationObserver callback){
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

    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          int minDistance,
                                          final ForegroundLocationObserver callback){
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

    public TokenForegroundLocationTracker(final WeakReference<? extends AppCompatActivity> weakReference,
                                          final ForegroundLocationObserver callback){
        startLocationTrackingSystem(weakReference, callback);
    }

    private void startLocationTrackingSystem(final WeakReference<? extends AppCompatActivity> weakReference,
                                             final ForegroundLocationObserver callback){
        this.weakReference = weakReference;
        this.locationTracker = callback;
        this.weakReference.get().getLifecycle().addObserver(this);
        if(bundle == null){
            bundle = new Bundle();
            bundle.putInt(MIN_METER_DISTANCE_FOR_UPDATE,this.minMeterDistanceForUpdate);
            bundle.putString(PREFERRED_LOCATION_TRACKER,this.preferredLocationTracker);
            bundle.putLong(MIN_MILLIS_TIME_FOR_UPDATE,this.minMillisTimeForUpdate);
        }
        if(preferredLocationTracker != null && (preferredLocationTracker.equals(LocationManager.GPS_PROVIDER) ||
                preferredLocationTracker.equals(LocationManager.NETWORK_PROVIDER) ||
                preferredLocationTracker.equals(LocationManager.PASSIVE_PROVIDER) ||
                preferredLocationTracker.equals(FUSED_PROVIDER))){
            String action = "com.tokeninc.locationtracker.REQUEST_LOCATION";
            startLocationIntent = new Intent();
            startLocationIntent.setComponent(new ComponentName(weakReference.get().getApplication().getPackageName(),
                    "com.tokeninc.locationtracker.ForegroundLocationTracker"));
            startLocationIntent.setAction(action);
            startLocationIntent.putExtras(bundle);
            weakReference.get().getApplicationContext().registerReceiver(foregroundBroadcastReceiver,
                    new IntentFilter("com.tokeninc.locationtracker.FOREGROUND_LOCATION_UPDATE"));
            weakReference.get().getApplicationContext().registerReceiver(foregroundBroadcastReceiver,
                    new IntentFilter("com.tokeninc.locationtracker.FOREGROUND_LOCATION_INFO"));
            weakReference.get().startService(startLocationIntent);
            //weakReference.get().bindService(startLocationIntent, connection, Context.BIND_AUTO_CREATE);
        }
        else{
            throw new IllegalArgumentException("LocationTrackerParameter is wrong,use LocationManager provided trackers instead");
        }
    }

    private BroadcastReceiver foregroundBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() != null){
                if(intent.getAction().equals("com.tokeninc.locationtracker.FOREGROUND_LOCATION_UPDATE") &&
                        intent.hasExtra("location")){
                    if(locationTracker != null){
                        locationTracker.onLocationUpdate(((Location) intent.getParcelableExtra("location")));
                    }
                }
                else if(intent.getAction().equals("com.tokeninc.locationtracker.FOREGROUND_LOCATION_INFO") &&
                        intent.hasExtra("error")){
                    if(locationTracker != null){
                        int val = intent.getIntExtra("error",Integer.MIN_VALUE);
                        if(val != Integer.MIN_VALUE){
                            locationTracker.onError(val);
                        }
                    }
                }
            }
        }
    };

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void stopService(){
        try{
            weakReference.get().stopService(startLocationIntent);
        }catch (Exception e){
            Log.getStackTraceString(e);
        }
    }
}
