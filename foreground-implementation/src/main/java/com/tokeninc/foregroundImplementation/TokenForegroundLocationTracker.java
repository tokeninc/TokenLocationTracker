package com.tokeninc.foregroundImplementation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class TokenForegroundLocationTracker {

    private Bundle bundle;
    private long minMillisTimeForUpdate = 1000L * 60; //A minute
    private int minMeterDistanceForUpdate = 50; //50 meters
    private String preferredLocationTracker = FUSED_PROVIDER;
    private static String MIN_MILLIS_TIME_FOR_UPDATE = "min_millis_time_for_update";
    private static String MIN_METER_DISTANCE_FOR_UPDATE = "min_meter_distance_for_update";
    private static String PREFERRED_LOCATION_TRACKER = "preferred_location_tracker";
    private static final String FUSED_PROVIDER = "fused";
    private ITokenForegroundLocationTracker locationTracker;
    private WeakReference<? extends AppCompatActivity> weakReference;
    private ServiceConnection connection;
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
                                          final IForegroundLocationObserver callback){
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
                                          final IForegroundLocationObserver callback){
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
                                          final IForegroundLocationObserver callback){
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
                                          final IForegroundLocationObserver callback){
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
                                          final IForegroundLocationObserver callback){
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
                                          final IForegroundLocationObserver callback){
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
                                          final IForegroundLocationObserver callback){
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
                                          final IForegroundLocationObserver callback){
        startLocationTrackingSystem(weakReference, callback);
    }

    private void startLocationTrackingSystem(final WeakReference<? extends AppCompatActivity> weakReference,
                                             final IForegroundLocationObserver callback){
        this.weakReference = weakReference;
        //this.weakReference.get().getLifecycle().addObserver(this);
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
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    locationTracker = ITokenForegroundLocationTracker.Stub.asInterface(service);
                    try{
                        locationTracker.registerCallback(callback);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    if(locationTracker != null){
                        try{
                            locationTracker.unRegisterCallback();
                        }catch (RemoteException e){
                            e.printStackTrace();
                        }
                    }

                }
            };
            weakReference.get().bindService(startLocationIntent, connection, Context.BIND_AUTO_CREATE);
        }
        else{
            throw new IllegalArgumentException("LocationTrackerParameter is wrong,use LocationManager provided trackers instead");
        }
    }
}
