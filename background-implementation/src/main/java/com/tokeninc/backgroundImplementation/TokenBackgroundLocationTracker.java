package com.tokeninc.backgroundImplementation;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class TokenBackgroundLocationTracker {

    public static String TOKEN_SHARED_PREF = "com.tokeninc.locationtracker.prefs";

    public TokenBackgroundLocationTracker(@NonNull WeakReference<? extends Application> context, @Nullable Data data){
        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build();
        PeriodicWorkRequest locationTracker;
        String className = "com.tokeninc.locationtracker.BackgroundLocationTracker";
        Class myClass;
        try{
            myClass = Class.forName(className);
            myClass.isAssignableFrom(ListenableWorker.class);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return;
        }
        if(data == null){
            data = new Data.Builder().putString("app_name",context.get().getPackageName()).build();
            locationTracker = new PeriodicWorkRequest.Builder(myClass,
                            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                            .setInputData(data).setConstraints(constraints).build();
        }
        else{
            Data.Builder builder = new Data.Builder();
            builder.putAll(data);
            builder.putString("app_name",context.get().getPackageName());
            data = builder.build();
            locationTracker = new PeriodicWorkRequest.Builder(myClass,
                            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                            .setConstraints(constraints).setInputData(data).build();
        }

        WorkManager.getInstance(context.get()).enqueueUniquePeriodicWork("locationTracker",
                ExistingPeriodicWorkPolicy.KEEP , locationTracker);
    }

    public synchronized void stopLocationTrackingInBackground(@NonNull Context context){
        WorkManager.getInstance(context).cancelUniqueWork("locationTracker");
    }

    public @Nullable Location getLastLocation(@NonNull Context context){
        SharedPreferences preferences =
                context.getSharedPreferences(TOKEN_SHARED_PREF,Context.MODE_PRIVATE);
        if(preferences == null){
            return null;
        }
        String provider = preferences.getString("provider",null);
        if(provider == null){
            return null;
        }
        else{
            Location location = new Location(provider);
            location.setAccuracy(preferences.getFloat("accuracy",Integer.MIN_VALUE));
            location.setAltitude(Double.longBitsToDouble(preferences.getLong("altitude",Integer.MIN_VALUE)));
            location.setBearing(preferences.getFloat("bearing",Integer.MIN_VALUE));
            location.setLatitude(Double.longBitsToDouble(preferences.getLong("latitude",Integer.MIN_VALUE)));
            location.setLongitude(Double.longBitsToDouble(preferences.getLong("longitude",Integer.MIN_VALUE)));
            location.setSpeed(preferences.getFloat("speed",Integer.MIN_VALUE));
            location.setTime(preferences.getLong("time",Integer.MIN_VALUE));
            return location;
        }
    }

    public static class BackgroundLocationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() != null &&
                    intent.getAction().equals("com.tokeninc.locationtracker.BACKGROUND_LOCATION_UPDATE") &&
            intent.getExtras() != null && intent.getExtras().getParcelable("location") != null){
                SharedPreferences preferences =
                        context.getSharedPreferences(TOKEN_SHARED_PREF,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                Location location = intent.getExtras().getParcelable("location");
                if(location != null){
                    editor.putString("provider",location.getProvider());
                    editor.putFloat("accuracy",location.getAccuracy());
                    editor.putLong("altitude",Double.doubleToRawLongBits(location.getAltitude()));
                    editor.putFloat("bearing",location.getBearing());
                    editor.putLong("latitude",Double.doubleToRawLongBits(location.getLatitude()));
                    editor.putLong("longitude",Double.doubleToRawLongBits(location.getLongitude()));
                    editor.putFloat("speed",location.getSpeed());
                    editor.putLong("time",location.getTime());
                    editor.commit();
                }
            }
        }
    }
}
