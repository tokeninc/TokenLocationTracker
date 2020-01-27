package com.tokeninc.locationtracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.content.ContextCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class BackgroundLocationTracker extends ListenableWorker {

    private LocationManager locationManager;
    private ListenableFuture<Result> result;
    private LocationInformationCallback callback;
    private int minDistance = 500;

    public BackgroundLocationTracker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        result = CallbackToFutureAdapter.getFuture(completer -> {
            locationManager = ((LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        15 * 60 * 1000L,
                        minDistance, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                if(callback != null){
                                    callback.onLocationUpdate(location);
                                    completer.set(Result.success());
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
                                if(callback != null){
                                    callback.onError(new IllegalStateException(provider + " is disabled"));
                                }
                                completer.set(Result.failure());
                            }
                        });
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
            else{
                completer.set(Result.failure());
            }
            return callback;
        });
        return result;
    }
}
