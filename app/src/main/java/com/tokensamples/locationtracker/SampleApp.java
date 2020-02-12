package com.tokensamples.locationtracker;

import android.app.Application;
import android.location.Location;

import com.tokeninc.backgroundImplementation.TokenBackgroundLocationTracker;

import java.lang.ref.WeakReference;

public class SampleApp extends Application {

    private TokenBackgroundLocationTracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        /*tracker = new TokenBackgroundLocationTracker(new WeakReference<>(this),null);
        Location location = tracker.getLastLocation(this);*/

    }
}
