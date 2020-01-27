package com.tokensamples.locationtracker;

import android.app.Application;

import androidx.work.Data;

import com.tokeninc.locationtracker.TokenLocationTracker;


public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Data.Builder builder = new Data.Builder();
        TokenLocationTracker.getInstance().startLocationTrackingInBackground(this,builder.build());
    }
}
