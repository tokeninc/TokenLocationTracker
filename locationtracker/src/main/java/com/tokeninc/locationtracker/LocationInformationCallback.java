package com.tokeninc.locationtracker;

import android.location.Location;

public interface LocationInformationCallback {
    void onLocationUpdate(Location location);
    void onError(Exception ex);
}
