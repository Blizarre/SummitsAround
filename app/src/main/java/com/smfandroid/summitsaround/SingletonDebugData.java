package com.smfandroid.summitsaround;

import java.util.Locale;

/**
 * Created by Sim on 14/12/2014.
 * This class will hold debugging data to be displayed or probed by the debugger
 */
public class SingletonDebugData {
    public int numberOfCompassRefresh = 0;
    public float rawInputAzimuth;
    public float rawInputPitch;
    public float rawInputRoll;
    public int compassAccuracy;
    public double rawInputLatitude;
    public double rawInputLongitude;
    public int numberOfGPSRefresh;
    public int numberOfPOI;
    private static SingletonDebugData m_singleton = null;
    public double angleCorrection;

    private SingletonDebugData() { }

    public static SingletonDebugData getInstance()
    {
        if(m_singleton == null)
            m_singleton = new SingletonDebugData();
        return m_singleton;
    }

    @Override
    public String toString()
    {
        return String.format(Locale.ENGLISH, "Compass raw: %02.3f (refresh#: %d)\nGPS raw lat: %03.4f, long: %03.4f (refresh#: %d)\nNumber of POI: %d\nAngle Correction: %.3f",
                rawInputAzimuth,
                numberOfCompassRefresh,
                rawInputLatitude,
                rawInputLongitude,
                numberOfGPSRefresh,
                numberOfPOI,
                angleCorrection);
    }
}
