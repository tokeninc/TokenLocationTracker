package com.tokensamples.locationtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.tokeninc.foregroundImplementation.ILocationObserver;
import com.tokeninc.foregroundImplementation.MyLocation;
import com.tokeninc.foregroundImplementation.TokenForegroundLocationTracker;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    AtomicBoolean doOnce = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*new TokenForegroundLocationTracker(new WeakReference<>(MainActivity.this), new ILocationObserver() {
            @Override
            public void onLocationUpdate(MyLocation location){
                if(!doOnce.getAndSet(true)){
                    startActivity(new Intent(MainActivity.this,Main2Activity.class));
                }
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
