package com.tokeninc.locationtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class LocationPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);
        Intent intent = getIntent();
        if(intent != null && intent.getAction() != null &&
                intent.getAction().equals(Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        else{
            throw new RuntimeException("Check parameters on intent");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 100:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyService();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Permission required to open location tracking system");
                    builder.setNegativeButton("OK", ((dialog, which) -> {
                        dialog.dismiss();
                        throw new RuntimeException("Permission required to open location tracking system");
                    }));
                }
            }
        }
    }

    private void notifyService(){
        final Intent intent = new Intent(this, ForegroundLocationTracker.class);
        intent.setAction(Manifest.permission.ACCESS_FINE_LOCATION);
        intent.putExtra("result",Activity.RESULT_OK);
        startService(intent);
        finish();
    }
}
