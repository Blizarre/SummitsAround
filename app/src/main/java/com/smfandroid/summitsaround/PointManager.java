package com.smfandroid.summitsaround;

import java.util.Vector;

import com.smfandroid.summitsaround.PointOfInterest.PointType;

import android.location.Location;

public class PointManager
{

	public Vector<GUIPointOfInterest> GetPointsForLocation(Location location, boolean showCompass)
	{
		Vector<GUIPointOfInterest> data = new Vector<GUIPointOfInterest>();

		// Default: North, South, East, West 
		if(showCompass)
		{
			data.add(new GUIPointOfInterest("North", PointType.NONE, 1000, Angle.A_ZERO));
			data.add(new GUIPointOfInterest("South", PointType.NONE, 1000, Angle.A_PI) );
			data.add(new GUIPointOfInterest("West" , PointType.NONE, 1000, Angle.A_HALF_PI ) );
			data.add(new GUIPointOfInterest("East" , PointType.NONE, 1000, Angle.A_THREE_HALF_PI ) );
		}

		return data;
	}
}