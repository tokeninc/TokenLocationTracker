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
                new WeakReference<AppCompatActivity>(this), new LocationInformationCallback() {
                    @Override
                    public void onLocationUpdate(Location location) {
                        ((TextView) findViewById(R.id.location_test_text)).setText(location.toString());
                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                });
    }
}
