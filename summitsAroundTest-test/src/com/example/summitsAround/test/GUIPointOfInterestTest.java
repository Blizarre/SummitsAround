package com.example.summitsAround.test;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.example.summitsAround.Angle;
import com.example.summitsAround.GUIPointOfInterest;
import com.example.summitsAround.PointOfInterest.PointType;

import junit.framework.TestCase;

public class GUIPointOfInterestTest extends TestCase {

	

	public void testGetDistance()
	{
		float distanceInKMeters = 1.0f;
		float distanceInMiles = 0.6237f;
		GUIPointOfInterest p = new GUIPointOfInterest("test", PointType.MONUMENT, distanceInKMeters, new Angle(0.5f));
		assertEquals(p.getDistance(false),distanceInKMeters, 0.01f);
		assertEquals(p.getDistance(true),distanceInMiles, 0.01f);
	}
	
	// Check that if sorted in a collection, the sort done using the angle 
	public void isComparable()
	{
		List<GUIPointOfInterest> l = new Vector<GUIPointOfInterest>();
		GUIPointOfInterest p1 = new GUIPointOfInterest("Test1",  PointType.MONUMENT, 10.0f, new Angle(0.5f));
		GUIPointOfInterest p2 = new GUIPointOfInterest("Test2",  PointType.MONUMENT, 10.0f, new Angle(0.9f));
		GUIPointOfInterest p3 = new GUIPointOfInterest("Test3",  PointType.MONUMENT, 10.0f, new Angle(0.1f));
		l.add(p1);
		l.add(p2);
		l.add(p3);
		
		Collections.sort(l);
		
		assertSame(l.get(0), p3);
		assertNotSame(l.get(1), p3);
		assertNotSame(l.get(2), p3);
		assertSame(l.get(1), p1);
		assertSame(l.get(2), p2);
	}
	
	public void testShouldDraw()
	{
		GUIPointOfInterest p1 = new GUIPointOfInterest("Test1",  PointType.MONUMENT, 10.0f, Angle.A_ZERO);
		Angle smallAngle = new Angle(0.1);
		
		assertTrue(p1.shouldDraw(Angle.A_PI, Angle.A_ZERO));
		assertTrue(p1.shouldDraw(Angle.A_PI, Angle.A_TWO_PI));
		
		assertTrue(p1.shouldDraw(smallAngle, new Angle(Angle.TWO_PI - 0.04)));
		assertTrue(p1.shouldDraw(smallAngle, new Angle(- 0.04)));
		assertTrue(p1.shouldDraw(smallAngle, new Angle(Angle.TWO_PI + 0.04)));
		
		assertFalse(p1.shouldDraw(smallAngle, new Angle(Angle.TWO_PI + 0.06) ));
		assertFalse(p1.shouldDraw(Angle.A_PI, new Angle(Math.PI + 0.01)));
		assertFalse(p1.shouldDraw(Angle.A_HALF_PI, new Angle(3.0 * Math.PI)));
	}

}
