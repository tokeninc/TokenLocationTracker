// IForegroundLocationObserver.aidl
package com.tokeninc.foregroundImplementation;

// Declare any non-default types here with import statements
import com.tokeninc.foregroundImplementation.MyLocation;
parcelable MyLocation;

interface IForegroundLocationObserver {

    void onLocationUpdate(out MyLocation location);

    // MyLocation object parameters via getter-setters
    // double mLatitude = 0.0;
    // double mLongitude = 0.0;
    // double mAltitude = 0.0f;
    // float mSpeed = 0.0f;
    // float mBearing = 0.0f;

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
