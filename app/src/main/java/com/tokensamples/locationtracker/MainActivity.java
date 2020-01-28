package com.tokensamples.locationtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.tokeninc.locationtracker.LocationInformationCallback;
import com.tokeninc.locationtracker.TokenLocationTracker;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TokenLocationTracker.getInstance().startLocationTracking(
                new WeakReference<>(MainActivity.this),1000L,100, new LocationInformationCallback() {
                    @Override
                    public void onLocationUpdate(Location location) {

                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });
    }
}
