package com.tokeninc.foregroundImplementation;

import android.location.Location;

public interface ForegroundLocationObserver {

    void onLocationUpdate(Location location);

    void onError(int errorCode);

    // Error Codes:
    // 1 : There are no location service opened,either all of them disabled or not running properly
    // 2 : Location Manager not properly set or null
    // 3 : GPS Provider Disabled
    // 4 : Network Provider Disabled
    // 5 : Passive Provider Disabled
    // 6 : GPS Provider Enabled
    // 7 : Network Provider Enabled
    // 8 : Passive Provider Enabled
}
