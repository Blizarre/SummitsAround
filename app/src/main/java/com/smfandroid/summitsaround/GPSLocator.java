package com.smfandroid.summitsaround;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


enum GPSAccuracy {
    BAD,
    GOOD
}

interface GPSLocatorListener {
    void updatedPosition(Location loc, GPSAccuracy accuracy);
}

public class GPSLocator implements LocationListener {

    protected LocationManager m_locManager;
    private GPSLocatorListener m_locator;

    public GPSLocator(Context c) {
        // Acquire a reference to the system Location Manager
        m_locManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
    }

    public void register(GPSLocatorListener loc) {
        m_locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        m_locator = loc;
    }


    @Override
    public void onLocationChanged(Location location) {
        GPSAccuracy acc = location.getAccuracy() < 20.0f ? GPSAccuracy.GOOD : GPSAccuracy.BAD;
        m_locator.updatedPosition(location, acc);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {  }

    @Override
    public void onProviderEnabled(String provider) {  }

    @Override
    public void onProviderDisabled(String provider) {  }

}
