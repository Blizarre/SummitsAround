package com.smfandroid.summitsaround.test;

import com.smfandroid.summitsaround.Angle;

import junit.framework.TestCase;

public class AngleTest  extends TestCase {

	public void testGetAngle()
	{
		Angle a1 = new Angle(5*Angle.PI);
		assertEquals(Angle.PI, a1.getRawAngle(), 0.01);
		
		Angle a2 = new Angle(-5*Angle.PI);
		assertEquals(Angle.PI, a2.getRawAngle(), 0.01);
		
		Angle a3 = new Angle(Angle.PI_OVER_FOUR);
		assertEquals(Angle.PI_OVER_FOUR, a3.getRawAngle(), 0.01);
		
		Angle a4 = new Angle(-Angle.PI_OVER_FOUR);
		assertEquals(7*Angle.PI_OVER_FOUR, a4.getRawAngle(), 0.01);
	}
	
	public void testCompare()
	{
		Angle a1 = new Angle(2.0d);
		Angle a2 = new Angle(3.0d);
		assertEquals(-1, a1.compareTo(a2));
		assertEquals(0, a1.compareTo(a1));
		assertEquals(1, a2.compareTo(a1));

		a1 = new Angle(Angle.PI_OVER_FOUR);
		a2 = new Angle(-Angle.PI_OVER_FOUR);
		assertEquals(-1, a1.compareTo(a2));
		assertEquals(0, a1.compareTo(a1));
		assertEquals(1, a2.compareTo(a1));
	}
	
}
