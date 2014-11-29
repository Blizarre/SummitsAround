package com.example.summitsAround;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

interface CompassListener
{
	/**
	 * See the documentation for function 
	 * 		float[] android.hardware.SensorManager.getOrientation(float[] R, float[] values)
	 * with "values" as [x, y, z]
	 * 
	 * @param x: azimuth, rotation around the Z axis. 
	 * @param y: pitch, rotation around the X axis. 
	 * @param z: roll, rotation around the Y axis. 
	 */
	void sensorDataChanged(float [] values);
}

public class Compass implements SensorEventListener {
	
	float[] mAccelerometerValues = new float[3];
	float[] mMagnetometerValues = new float[3];
	
	float[] lastAzimuth = new float[10];
	int m_index = 0;
	
	SensorManager m_sensorManager = null;
	Sensor m_compass = null, m_accelerometer = null;
	CompassListener m_Compasslistener;
	
	Compass(Context context, CompassListener listener) {
		m_Compasslistener = listener;
		m_sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		m_compass = m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		m_accelerometer = m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		m_sensorManager.registerListener(this, m_compass, SensorManager.SENSOR_DELAY_GAME);
	    m_sensorManager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_UI);
	}

	 
	/*
	 * Nothing to see here right now
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		return;
	}

	public void onSensorChanged(SensorEvent sensorEvent) {
		float[] values = new float[3];
		float[] R = new float[9];

		switch(sensorEvent.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			for(int i = 0; i < 3; i++){
				mAccelerometerValues[i] = sensorEvent.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for(int i = 0; i < 3; i++){
				mMagnetometerValues[i] = sensorEvent.values[i];
			}
			break;
		}

		SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagnetometerValues);
		SensorManager.getOrientation(R, values);
		
		// The compass will give 0.0 when the top of the phone is facing North. However, the user user is holding 
		// the phone looking at the screen, so, for him the phone is facing West. Remove PI/2 to get to the 
		// user's perspective 
		values[0] += Math.PI / 2.0d;
		
		// dampening algorithm: first try 
		lastAzimuth[m_index%lastAzimuth.length] = values[0];
		m_index ++;
		values[0] = mean(lastAzimuth);
		
		m_Compasslistener.sensorDataChanged(values);
	}
	
	float mean(float[] array) { 
		float sum = 0;
		for(float val: array)
		{
			sum += val;
		}
		return sum / array.length;
	}
}