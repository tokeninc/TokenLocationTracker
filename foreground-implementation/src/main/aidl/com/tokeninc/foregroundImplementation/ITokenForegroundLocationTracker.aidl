// ITokenForegroundLocationTracker.aidl
package com.tokeninc.foregroundImplementation;

// Declare any non-default types here with import statements
import com.tokeninc.foregroundImplementation.IForegroundLocationObserver;
interface ITokenForegroundLocationTracker {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerCallback(IForegroundLocationObserver callback);
    void unRegisterCallback();
}
