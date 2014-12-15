package com.smfandroid.summitsaround;

import android.graphics.PointF;

import com.smfandroid.summitsaround.PointOfInterest.PointType;

// TODO: Find a way to reduce redundant computations (currentAngle, widthNorm)

public class GUIPointOfInterest implements Comparable<GUIPointOfInterest> {
    PointType mType;
    Angle mAngle;
    float mDistance;
    String mLabel;

    public GUIPointOfInterest(String name, PointType type, float distance, Angle angle) {
        mAngle = angle;
        mDistance = distance;
        mLabel = name;
        mType = type;
    }

    public PointType getType() {
        return mType;
    }

    public String getLabel() {
        return mLabel;
    }

    public Angle getAngle() {
        return mAngle;
    }

    public float getDistance(boolean inMiles) {
        return inMiles ? mDistance * 0.6214f : mDistance;
    }

    @Override
    public int compareTo(GUIPointOfInterest another) {
        return this.getAngle().compareTo(another.getAngle());
    }

    /*
     * Check whether the GUIPOI is is the screen space.
     */
    public boolean shouldDraw(Angle cameraOpenAngle, Angle cameraAngle) {
        Angle currentAngle = getAngle().sub(cameraAngle);
        if (currentAngle.getRawAngle() > Angle.PI)
            currentAngle = new Angle(0).sub(currentAngle);

        Angle angleMax = cameraOpenAngle.mul(0.5);

        return currentAngle.compareTo(angleMax) <= 0;
    }

    /***
     * Return the position of the item in the screen space
     * @param cameraOpenAngle Camera FOV angle (Angle type)
     * @param screenWidth Width of the screen (pixels)
     * @param screenHeight Height of the screen (pixels)
     * @param deviceAzimuth Azimuth of the device
     * @return
     */
    public PointF getPositionInGUI(Angle cameraOpenAngle, float screenWidth, float screenHeight, Angle deviceAzimuth) {
        Angle currentAngle = getAngle().sub(deviceAzimuth);
        PointF position = new PointF();
        double widthNorm = screenWidth / cameraOpenAngle.sin();

        position.x = screenWidth / 2.0f + (float) (currentAngle.sin() * widthNorm);
        position.y = screenHeight; // For now the altitude is not computed
        return position;
    }
}

