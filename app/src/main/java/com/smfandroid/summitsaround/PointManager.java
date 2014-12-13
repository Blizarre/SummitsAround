package com.smfandroid.summitsaround;

import android.location.Location;

import com.smfandroid.summitsaround.PointOfInterest.PointType;

import java.util.Vector;

/**
 * File format definition:
 * [
 * { "name":"Mairie de Villeurbanne", "altitude":100, "latitude":45.766592, "longitude":4.879600, "type":"BUILDING", "areas"=["Rhône", "France"] },
 * { "name":"Tour Part Dieu", "altitude":100, "latitude":45.761063, "longitude":4.853752, "type":"BUILDING", "areas"=["Rhône", "France"] },
 * { "name":"Basilique de fourvière", "altitude":100, "latitude":45.762262, "longitude":4.822910, "type":"BUILDING", "areas"=["Rhône", "France"] }
 * { "name":"Mont Blanc", "altitude":100, "latitude":45.762262, "longitude":4.822910, "type":"BUILDING", "areas"=["Rhône", "France"] }
 * ]
 */

public class PointManager {
    protected Vector<PointOfInterest> m_pointsOfInterest = new Vector<>();

    public PointManager() {
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Mairie de Villeurbanne", createLocation(45.766592, 4.879600, 100.0)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Tour Part Dieu", createLocation(45.761063, 4.853752, 100.0)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Basilique de fourvière", createLocation(45.762262, 4.822910, 100.0)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Mont Blanc", createLocation(45.832956, 6.865066, 4810)));
    }

    protected Location createLocation(double latitude, double longitude, double altitude) {
        Location l = new Location("DUMMYPROVIDER");
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        l.setAltitude(altitude);
        return l;
    }

    public Vector<GUIPointOfInterest> GetPointsForLocation(Location location, boolean showCompass) {
        Vector<GUIPointOfInterest> data = new Vector<>();

        for (PointOfInterest p : m_pointsOfInterest)
            data.add(new GUIPointOfInterest(p.getLabel(), p.getType(), p.computeDistanceFrom(location), p.computeAngleFrom(location)));

        // Default: North, South, East, West
        if (showCompass) {
            data.add(new GUIPointOfInterest("North", PointType.NONE, 1000, Angle.A_ZERO));
            data.add(new GUIPointOfInterest("South", PointType.NONE, 1000, Angle.A_PI));
            data.add(new GUIPointOfInterest("West", PointType.NONE, 1000, Angle.A_HALF_PI));
            data.add(new GUIPointOfInterest("East", PointType.NONE, 1000, Angle.A_THREE_HALF_PI));
        }

        return data;
    }
}