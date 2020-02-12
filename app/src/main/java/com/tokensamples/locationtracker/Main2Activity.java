package com.tokensamples.locationtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.IBinder;

import com.tokeninc.foregroundImplementation.ILocationObserver;
import com.tokeninc.foregroundImplementation.MyLocation;
import com.tokeninc.foregroundImplementation.TokenForegroundLocationTracker;

import java.lang.ref.WeakReference;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        /*new TokenForegroundLocationTracker(new WeakReference<>(Main2Activity.this), new ILocationObserver() {
            @Override
            public void onLocationUpdate(MyLocation location){

            }

            @Override
            public void onError(int errorCode){

            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        });*/
    }
}
