package com.smfandroid.summitsaround;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.smfandroid.summitsaround.PointOfInterest.PointType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class PointManager {
    protected final String TAG = getClass().getSimpleName();

    protected Vector<PointOfInterest> m_pointsOfInterest = new Vector<>();

    public void setPrefs(SharedPreferences prefs) {
        this.m_prefs = prefs;
    }

    public SharedPreferences getPrefs() {return this.m_prefs;}

    SharedPreferences m_prefs;
    Context m_Context;

    public PointManager(Context c) {
        m_Context = c;
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
            for (PointOfInterest p : m_pointsOfInterest) {
                if ( p.computeDistanceFrom(location) < Integer.parseInt(m_prefs.getString("distance", "0"))) {
                    data.add(new GUIPointOfInterest(p.getLabel(), p.getType(),
                            p.computeDistanceFrom(location), p.computeAngleFrom(location)));
                }
            }
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
        // Store the list of  all PointTypes enabled in the configuration. the config name must be pt_<PointTypeName>
        Set<PointType> enabledPT = new HashSet<>();
        for(PointType pt : PointType.values())
        {
            if(m_prefs.getBoolean("pt_" + pt.name(), false))
            {
                enabledPT.add(pt);
            }
        }

        // Parse the JSON file and extract only the landmarks that are enabled un the preferences
        JSONObject obj = new JSONObject(data);
        JSONArray jArray = obj.getJSONArray("POIList");
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jo_inside = jArray.getJSONObject(i);
            String name = jo_inside.getString("name");
            PointType type;
            try {
                type = PointType.valueOf(jo_inside.getString("type"));
            } catch (IllegalArgumentException exc)
            {
                Log.e(TAG, "Invalid type during json loading:" + jo_inside.getString("type"));
                type = PointType.NONE;
            }
            double altitude = Double.parseDouble(jo_inside.getString("altitude"));
            double latitude =  Double.parseDouble(jo_inside.getString("latitude"));
            double longitude = Double.parseDouble(jo_inside.getString("longitude"));
            if (enabledPT.contains(type))
            {
                    m_pointsOfInterest.add(new PointOfInterest(type, name,
                            createLocation(latitude, longitude, altitude)));
            }
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


    public void reset() {
        m_pointsOfInterest.clear();
        try {
            InputStreamReader dataFile = new InputStreamReader( m_Context.getResources().openRawResource(R.raw.sampledata) );
            this.loadFromJson(dataFile);
        }
        catch (Exception e) {
            Log.i(TAG, "JSON Data file not found. Back to default");
            this.loadDefaultData();
        }
        SingletonDebugData.getInstance().numberOfPOI = m_pointsOfInterest.size();
    }
}

