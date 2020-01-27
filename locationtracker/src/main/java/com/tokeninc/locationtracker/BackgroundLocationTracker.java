package com.tokeninc.locationtracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.BatteryManager;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.content.ContextCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class BackgroundLocationTracker extends ListenableWorker {

    private LocationManager locationManager;
    //private LocationCallback locationCallback;
    private ListenableFuture<Result> result;

    public BackgroundLocationTracker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        /*result = CallbackToFutureAdapter.getFuture(completer -> {
            locationManager = ((LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE));
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(request,locationCallback,getApplicationContext().getMainLooper());
            }
            else{
                completer.set(Result.failure());
            }
            return locationCallback;
        });
        return result;*/
        return null;
    }
}
