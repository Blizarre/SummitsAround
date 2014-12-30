package com.smfandroid.summitsaround;

import android.location.Location;


public class PointOfInterest {
    // Type of the PointOfInterest
    PointType mType;
    // Short description
    String mLabel;
    Location mLocation;

    public PointOfInterest(PointType type, String label, Location loc) {
        mLabel = label;
        mType = type;
        mLocation = loc;
    }

    public PointType getType() {
        return mType;
    }

    public String getLabel() {
        return mLabel;
    }

    public Location getLocation() {
        return mLocation;
    }

    public Angle computeAngleFrom(Location other) {
        // Since we will compute angles from points that are fairly close (< 10km),
        // we approximate the angle by working on a planar surface
        double diffLatitude = mLocation.getLatitude() - other.getLatitude();
        double diffLongitude = mLocation.getLongitude() - other.getLongitude();
        return new Angle(Math.atan2(diffLongitude, diffLatitude));
    }

    /***
     * Compute the distance between this PointOfInterest and the location other,
     * @param other Other point
     * @return distance in kilometer between this and other
     */
    public float computeDistanceFrom(Location other) {
        return (float) (other.distanceTo(mLocation) / 1000.0d);
    }

    /***
     * Return a GUIPointOfInterest suitable for drawing on the GUI
     * @param reference the location of the device used as a reference.
     * @return a GUIPointOfInterest. Its values are derived from this.
     */
    public GUIPointOfInterest computeGUIPointOfInterest(Location reference) {
        return new GUIPointOfInterest(getLabel(), getType(), computeDistanceFrom(reference), computeAngleFrom(reference));
    }


    public enum PointType {
        SUMMIT,
        TOWN,
        MONUMENT,
        BUILDING,
        NONE
    }
}