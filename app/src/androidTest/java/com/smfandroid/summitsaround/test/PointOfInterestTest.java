package com.smfandroid.summitsaround.test;

import com.smfandroid.summitsaround.*;

import android.location.Location;
import junit.framework.TestCase;

public class PointOfInterestTest extends TestCase {
	
	public void testComputeDistanceFrom()
	{
		Location testLocation1 = new Location("TEST");
		Location testLocation2 = new Location("TEST");
		
		// Some place in Paris: 48.875797, 2.326461
		testLocation1.setLatitude(48.875797d);
		testLocation1.setLongitude(2.326461d);
		
		// Another place in Paris: 48.857391, 2.374354
		testLocation2.setLatitude(48.857391d);
		testLocation2.setLongitude(2.374354d);
		
		PointOfInterest p = new PointOfInterest(PointOfInterest.PointType.MONUMENT, "test", testLocation1);
		
		// Google maps says around 4.0 kilo meters, with a 100m error allowed
		assertEquals(4.0, p.computeDistanceFrom(testLocation2), 0.1);
		
		// Should be zero, compute distance with himself
		assertEquals(0, p.computeDistanceFrom(testLocation1), 1);
	}
	
	public void testComputeAngleFrom()
	{
		// testLocation2 is north of testLocation1,
		// testLocation3 is west of testLocation2
		Location testLocation1 = new Location("TEST");
		Location testLocation2 = new Location("TEST");
		Location testLocation3 = new Location("TEST");

		testLocation1.setLongitude(5.0);
		testLocation2.setLongitude(5.0);
		testLocation3.setLongitude(15.0);
		
		testLocation1.setLatitude(5.0);
		testLocation2.setLatitude(15.0);
		testLocation3.setLatitude(15.0);
		
		// testLocation1 is north of testLocation2
		PointOfInterest p1 = new PointOfInterest(PointOfInterest.PointType.MONUMENT, "test", testLocation1);
		assertEquals(0, p1.computeAngleFrom(testLocation2).compareTo(Angle.A_ZERO));

		// testLocation2 is South of testLocation1		
		PointOfInterest p2 = new PointOfInterest(PointOfInterest.PointType.MONUMENT, "test", testLocation2);
		assertEquals(0, Angle.A_PI.compareTo(p2.computeAngleFrom(testLocation1)));
		
		// testLocation3 is west of testLocation2		
		PointOfInterest p3 = new PointOfInterest(PointOfInterest.PointType.MONUMENT, "test", testLocation2);
		assertEquals(0, Angle.A_HALF_PI.compareTo(p3.computeAngleFrom(testLocation3)));
		
		// testLocation2 is east of testLocation3		
		PointOfInterest p4 = new PointOfInterest(PointOfInterest.PointType.MONUMENT, "test", testLocation3);
		assertEquals(0, Angle.A_THREE_HALF_PI.compareTo( p4.computeAngleFrom(testLocation2)));
	}

	public void testComputeGUIPointOfInterest()
	{
		// testLocation2 is north of testLocation1,
		Location testLocation1 = new Location("TEST");
		Location testLocation2 = new Location("TEST");

		testLocation1.setLongitude(5.0);
		testLocation2.setLongitude(5.0);
		
		testLocation1.setLatitude(5.0);
		testLocation2.setLatitude(15.0);
		PointOfInterest p1 = new PointOfInterest(PointOfInterest.PointType.MONUMENT, "test", testLocation1);

		GUIPointOfInterest gp1 = p1.computeGUIPointOfInterest(testLocation2);
		assertEquals(gp1.getAngle(), p1.computeAngleFrom(testLocation2));
		assertEquals(gp1.getDistance(false), p1.computeDistanceFrom(testLocation2));
		assertEquals(gp1.getLabel(), p1.getLabel());
		assertEquals(gp1.getType(), p1.getType());
	}
}
