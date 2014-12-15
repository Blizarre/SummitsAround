package com.smfandroid.summitsaround;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

interface CompassListener {
    /**
     * See the documentation for function
     * float[] android.hardware.SensorManager.getOrientation(float[] R, float[] values)
     * with "values" as [x, y, z]
     *
     * @param azimuth rotation around the Z axis
     * @param pitch rotation around the X axis
     * @param roll, rotation around the Y axis
     */
    void sensorDataChanged(Angle azimuth, Angle pitch, Angle roll);
}

public class Compass implements SensorEventListener {

    float[] mAccelerometerValues = new float[3];
    float[] mMagnetometerValues = new float[3];

    double[] lastAzimuth = new double[10];
    int m_index = 0;

    SensorManager m_sensorManager = null;
    Sensor m_compass = null, m_accelerometer = null;
    CompassListener m_compasslistener;

    Compass(Context context, CompassListener listener) {
        m_compasslistener = listener;
        m_sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        m_compass = m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        m_accelerometer = m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        m_sensorManager.registerListener(this, m_compass, SensorManager.SENSOR_DELAY_GAME);
        m_sensorManager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_UI);
    }


    /*
     * Nothing to see here right now
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = new float[3];
        float[] R = new float[9];
        Angle azimuth, pitch, roll;

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for (int i = 0; i < 3; i++) {
                    mAccelerometerValues[i] = sensorEvent.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (int i = 0; i < 3; i++) {
                    mMagnetometerValues[i] = sensorEvent.values[i];
                }
                break;
        }

        SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagnetometerValues);
        SensorManager.getOrientation(R, values);

        /* For debugging purpose */
        SingletonDebugData debug = SingletonDebugData.getInstance();
        debug.rawInputAzimuth = values[0];
        debug.rawInputPitch = values[1];
        debug.rawInputRoll = values[2];
        debug.numberOfCompassRefresh ++;
        /* / For debugging purpose */


        // The compass will give 0.0 when the top of the phone is facing North. However, the user user is holding
        // the phone looking at the screen, so, for him the phone is facing East. Add PI/2 to get to the
        // user's perspective
        azimuth = new Angle(values[0] + Angle.HALF_PI);
        pitch = new Angle(values[1]);
        roll = new Angle(values[2]);

        // dampening algorithm: first try
        lastAzimuth[m_index % lastAzimuth.length] = azimuth.getRawAngle();
        m_index++;
        azimuth.setAngle( mean(lastAzimuth) );

        m_compasslistener.sensorDataChanged(azimuth, pitch, roll);
    }


    /***
     * Compute the mean of an array of angles (as double). This code needs to use a little trick to
     * be able to compute the mean when some angles are 0 and the other are 2*PI.
     * (the naIve method will average 0 and 2PI as PI, even if Angle(0) == Angle(2PI))
     * We use the fact that all angles will be close, with max(angle) - min(angle) < PI.
     * We bring all the smaller values on the same range as the bigger one by adding 2*PI.
     * If the spread is too large, the average will return garbage anyway, so there is no need
     * to use a more computationally-demanding algorithm.
     * @param array array of angles to be averaged. Warning, not for the general case !
     * @return the averaged Angle
     */
    double  mean(double[] array) {
        boolean oneIsOver3HalfPi = false;
        double sum = 0;

        for (double val : array) {
            sum += val;
            if(val > Angle.THREE_HALF_PI) { // Some values may be > 2PI and back to 0
                oneIsOver3HalfPi = true;
                break; // break here and re-compute the main in the worst-case scenario
            }
        }

        // Worst-cas scenario
        if(oneIsOver3HalfPi)
        {
            sum = 0;
            for (double val : array) {
                if(val < Angle.HALF_PI) // we know angles > 3PI/2 exists, so we will translate the smaller one
                    sum += val + Angle.TWO_PI;
                else
                    sum += val;
            }
        }
        return sum/array.length;
    }
}