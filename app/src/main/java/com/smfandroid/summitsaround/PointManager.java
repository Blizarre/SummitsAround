package com.smfandroid.summitsaround;

import java.util.Vector;

import com.smfandroid.summitsaround.PointOfInterest.PointType;

import android.location.Location;

/***
 * File format definition:
 * [
 * { "name":"Mairie de Villeurbanne", "altitude":100, "latitude":45.766592, "longitude":4.879600, "type":"BUILDING", "areas"=["Rhône", "France"] },
 * { "name":"Tour Part Dieu", "altitude":100, "latitude":45.761063, "longitude":4.853752, "type":"BUILDING", "areas"=["Rhône", "France"] },
 * { "name":"Basilique de fourvière", "altitude":100, "latitude":45.762262, "longitude":4.822910, "type":"BUILDING", "areas"=["Rhône", "France"] }
 * { "name":"Mont Blanc", "altitude":100, "latitude":45.762262, "longitude":4.822910, "type":"BUILDING", "areas"=["Rhône", "France"] }
 * ]
 */

public class PointManager
{

	public Vector<GUIPointOfInterest> GetPointsForLocation(Location location, boolean showCompass)
	{
		Vector<GUIPointOfInterest> data = new Vector<>();

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