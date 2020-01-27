package com.tokeninc.locationtracker;

public enum NetworkStatus{
    GPS(false),
    NETWORK(false),
    PASSIVE(false);

    private boolean status;

    NetworkStatus(boolean status){
        this.status = status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }
}
