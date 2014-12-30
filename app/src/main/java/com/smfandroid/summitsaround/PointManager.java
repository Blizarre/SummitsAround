package com.smfandroid.summitsaround;

import android.location.Location;

import com.smfandroid.summitsaround.PointOfInterest.PointType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * File format definition:
 * { "POIList": [
 * { "name":"Mairie de Villeurbanne", "altitude":100, "latitude":45.766592, "longitude":4.879600, "type":"BUILDING", "areas"=["Rhône", "France"] },
 * { "name":"Tour Part Dieu", "altitude":100, "latitude":45.761063, "longitude":4.853752, "type":"BUILDING", "areas"=["Rhône", "France"] },
 * { "name":"Basilique de fourvière", "altitude":100, "latitude":45.762262, "longitude":4.822910, "type":"BUILDING", "areas"=["Rhône", "France"] }
 * { "name":"Mont Blanc", "altitude":100, "latitude":45.833679, "longitude":6.864564, "type":"BUILDING", "areas"=["Rhône", "France"] }
 * ] }
 */

public class PointManager {
    protected Vector<PointOfInterest> m_pointsOfInterest = new Vector<>();

    public PointManager() {
    }

    protected Location createLocation(double latitude, double longitude, double altitude) {
        Location l = new Location("DUMMYPROVIDER");
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        l.setAltitude(altitude);
        return l;
    }

    /***
     * Prepare a list of all the points of interest displayable for the current location.
     * @param location The location used to generate the GUIPointOfInterest. If null, will only return the cardinal points
     *                 if showCompass is True, or an empty list if showCompass is False.
     * @param showCompass if True, include in the GUIPointOfInterest Vector the four cardinal points
     * @return A vector of GUIPointOfInterest that should be displayed at the current location
     */
    public Vector<GUIPointOfInterest> GetPointsForLocation(Location location, boolean showCompass) {
        Vector<GUIPointOfInterest> data = new Vector<>();

        if(location != null) {
            for (PointOfInterest p : m_pointsOfInterest)
                data.add(new GUIPointOfInterest(p.getLabel(), p.getType(), p.computeDistanceFrom(location), p.computeAngleFrom(location)));
        }

        // Default: North, South, East, West
        if (showCompass) {
            data.add(new GUIPointOfInterest("North", PointType.NONE, 1000, Angle.A_ZERO));
            data.add(new GUIPointOfInterest("South", PointType.NONE, 1000, Angle.A_PI));
            data.add(new GUIPointOfInterest("West", PointType.NONE, 1000, Angle.A_THREE_HALF_PI));
            data.add(new GUIPointOfInterest("East", PointType.NONE, 1000, Angle.A_HALF_PI));
        }

        return data;
    }

    public void loadFromJson(InputStreamReader rd) throws JSONException, IOException {
        StringBuilder strB = new StringBuilder();
        int len;
        char buffer[] = new char[1024 * 16];
        while(rd.ready()) {
            len = rd.read(buffer);
            strB.append(buffer, 0, len);
        }
        loadFromJson(strB.toString());
    }

    // TODO: switch to JsonReader, much better scalability
    public void loadFromJson(String data) throws JSONException {
        JSONObject obj = new JSONObject(data);
        JSONArray jArray = obj.getJSONArray("POIList");
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jo_inside = jArray.getJSONObject(i);
            String name = jo_inside.getString("name");
            String type = jo_inside.getString("type");
            double altitude = Double.parseDouble(jo_inside.getString("altitude"));
            double latitude =  Double.parseDouble(jo_inside.getString("latitude"));
            double longitude = Double.parseDouble(jo_inside.getString("longitude"));
            /*if(jo_inside.has("areas")) {
                JSONArray areasArr = jo_inside.getJSONArray("areas");
                Vector<String> areas = new Vector<>(); // not used right now
                for (int j = 0; j < areasArr.length(); ++j)
                {
                    areas.add(areasArr.getString(j));
                }
            }*/
            m_pointsOfInterest.add(new PointOfInterest(PointType.valueOf(type), name,
                    createLocation(latitude, longitude, altitude)));
        }
    }


    public Vector<PointOfInterest> getPointList() {
        return m_pointsOfInterest;
    }

    public void loadDefaultData() {
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Mairie de Villeurbanne", createLocation(45.766592, 4.879600, 100.0)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Tour Part Dieu", createLocation(45.761063, 4.853752, 100.0)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Basilique de Fourvière", createLocation(45.762262, 4.822910, 100.0)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Mont Blanc", createLocation(45.833679, 6.864564, 4810)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Tour Incity", createLocation(45.763360, 4.850961, 100.0)));
        m_pointsOfInterest.add(new PointOfInterest(PointType.MONUMENT, "Opéra de Lyon", createLocation(45.767792, 4.836611, 100.0)));
    }
}