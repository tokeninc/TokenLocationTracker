package com.tokeninc.locationtracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class BackgroundLocationTracker extends ListenableWorker {

    private LocationManager locationManager;
    private ListenableFuture<Result> result;
    private LocationListener listener;
    private int minDistance = 500;
    private String appName;

    public BackgroundLocationTracker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        result = CallbackToFutureAdapter.getFuture(completer -> {
            appName = getInputData().getString("app_name");
            locationManager = ((LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("location",location);
                        if(appName != null){
                            intent.setComponent(new ComponentName(appName,
                                    appName + ".TokenBackgroundLocationTracker"));
                        }
                        intent.setAction("com.tokeninc.locationtracker.BACKGROUND_LOCATION_UPDATE");
                        getApplicationContext().sendBroadcast(intent);
                        completer.set(Result.success());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        completer.set(Result.failure());
                    }
                };
                locationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        15 * 60 * 1000L, minDistance, listener);
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            else{
                completer.set(Result.failure());
            }
            return listener;
        });
        return result;
    }
}
