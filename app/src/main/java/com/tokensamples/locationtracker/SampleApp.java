package com.tokensamples.locationtracker;

import android.app.Application;

import com.tokeninc.backgroundImplementation.TokenBackgroundLocationTracker;

import java.lang.ref.WeakReference;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new TokenBackgroundLocationTracker(new WeakReference<>(this));
    }
}
