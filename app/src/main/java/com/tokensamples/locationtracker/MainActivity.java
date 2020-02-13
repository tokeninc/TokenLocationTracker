package com.tokensamples.locationtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;

import com.tokeninc.foregroundImplementation.ForegroundLocationObserver;
import com.tokeninc.foregroundImplementation.TokenForegroundLocationTracker;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new TokenForegroundLocationTracker(new WeakReference<>(MainActivity.this), new ForegroundLocationObserver() {
            @Override
            public void onLocationUpdate(Location location) {

            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }
}
