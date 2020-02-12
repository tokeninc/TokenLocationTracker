package com.tokeninc.foregroundImplementation;

import android.os.Parcel;
import android.os.Parcelable;

public class MyLocation implements Parcelable {

    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private double mAltitude = 0.0f;
    private float mSpeed = 0.0f;
    private float mBearing = 0.0f;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mAltitude);
        dest.writeFloat(this.mSpeed);
        dest.writeFloat(this.mBearing);
    }

    public MyLocation(){
        super();
    }

    public MyLocation(double mLatitude,double mLongitude,double mAltitude,float mSpeed,
                      float mBearing) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mAltitude = mAltitude;
        this.mSpeed = mSpeed;
        this.mBearing = mBearing;
    }

    private MyLocation(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in){
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mAltitude = in.readDouble();
        this.mSpeed = in.readFloat();
        this.mBearing = in.readFloat();
    }

    public static final Creator<MyLocation> CREATOR = new Creator<MyLocation>() {
        @Override
        public MyLocation createFromParcel(Parcel source) {
            return new MyLocation(source);
        }

        @Override
        public MyLocation[] newArray(int size) {
            return new MyLocation[size];
        }
    };

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setmAltitude(double mAltitude) {
        this.mAltitude = mAltitude;
    }

    public void setmSpeed(float mSpeed) {
        this.mSpeed = mSpeed;
    }

    public void setmBearing(float mBearing) {
        this.mBearing = mBearing;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public double getmAltitude() {
        return mAltitude;
    }

    public float getmSpeed() {
        return mSpeed;
    }

    public float getmBearing() {
        return mBearing;
    }
}
