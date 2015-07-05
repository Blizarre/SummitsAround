package com.smfandroid.summitsaround.test;

import com.smfandroid.summitsaround.*;

import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.Vector;

public class PointManagerTest extends AndroidTestCase {


    public void testLoadFromJSON() throws JSONException {
        PointManager pm = new PointManager(getContext());
        String testData =
            "{ POIList:[ " +
            "{ \"name\":\"Mairie de Villeurbanne\", \"altitude\":100, \"latitude\":45.766592, \"longitude\":4.879600, \"type\":\"BUILDING\", \"areas\" : [\"Rhône\", \"France\"] }," +
            "{ \"name\":\"Tour Part Dieu\", \"altitude\":100, \"latitude\":45.761063, \"longitude\":4.853752, \"type\":\"BUILDING\", \"areas\" : [\"Rhône\", \"France\"] }," +
            "{ \"name\":\"Basilique de fourvière\", \"altitude\":100, \"latitude\":45.762262, \"longitude\":4.822910, \"type\":\"BUILDING\", \"areas\": [ \"Rhône\", \"France\" ] }," +
            "{ \"name\":\"Mont Blanc\", \"altitude\":100, \"latitude\":45.833679, \"longitude\":6.864564, \"type\":\"SUMMIT\", \"areas\" : [\"Rhône\", \"France\"] }" +
            "]}";

        pm.loadFromJson(testData);

        Vector<PointOfInterest> listOfPoi = pm.getPointList();
        assertEquals(4, listOfPoi.size());
        assertEquals("Mairie de Villeurbanne", listOfPoi.get(0).getLabel());
        assertEquals(listOfPoi.get(0).getLocation().getLongitude(),4.879600, 0.0001);
        assertEquals(listOfPoi.get(0).getLocation().getLatitude(),45.766592, 0.0001);
        assertEquals(listOfPoi.get(0).getLocation().getAltitude(),100, 0.001);
        assertEquals(PointOfInterest.PointType.BUILDING, listOfPoi.get(0).getType());

        assertEquals("Basilique de fourvière", listOfPoi.get(2).getLabel());

        assertEquals("Mont Blanc", listOfPoi.get(3).getLabel());
        assertEquals(PointOfInterest.PointType.SUMMIT, listOfPoi.get(3).getType());
        assertEquals(listOfPoi.get(3).getLocation().getLatitude(),45.833679, 0.0001);
        assertEquals(listOfPoi.get(3).getLocation().getLongitude(),6.864564, 0.0001);

        boolean exceptionHappened = false;
        try {
            pm.loadFromJson("{invalid}");
        } catch (JSONException e)
        {
            exceptionHappened = true;
        }
        assertTrue(exceptionHappened);
    }
}
