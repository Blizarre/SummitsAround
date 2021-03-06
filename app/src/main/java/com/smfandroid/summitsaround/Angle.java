package com.smfandroid.summitsaround;

import android.support.annotation.NonNull;

import java.util.Locale;

@SuppressWarnings("UnusedDeclaration")
public class Angle implements Comparable<Angle> {

    // Raw angle static definitions
    public static final double HALF_PI = Math.PI / 2.0d;
    public static final double THREE_HALF_PI = 3.0d * Math.PI / 2.0d;
    public static final double PI = Math.PI;
    public static final double ZERO = 0.0d;
    public static final double TWO_PI = 2.0d * Math.PI;
    public static final double PI_OVER_FOUR = Math.PI / 4.0d;

    // Object angle static definitions
    public static final Angle A_HALF_PI = new Angle(Math.PI / 2.0d);
    public static final Angle A_THREE_HALF_PI = new Angle(3.0d * Math.PI / 2.0d);
    public static final Angle A_PI = new Angle(Math.PI);
    public static final Angle A_TWO_PI = new Angle(2.0d * Math.PI);
    public static final Angle A_ZERO = new Angle(0.0d);
    public static final Angle A_PI_OVER_FOUR = new Angle(Math.PI / 4.0d);
    public static Angle ANGLE_PI = new Angle(Math.PI);
    double mAngle; // radiant

    public Angle(double angle) {
        mAngle = angle;
    }

    public Angle() {
        mAngle = 0.0d;
    }

    public Angle add(Angle other) {
        return new Angle(mAngle + other.mAngle);
    }

    public Angle sub(Angle other) {
        return new Angle(mAngle - other.mAngle);
    }

    public void mul_inplace(double val) {
        mAngle *= val;
    }

    public void add_inplace(Angle other) {
        mAngle += other.mAngle;
    }

    public void sub_inplace(Angle other) {
        mAngle -= other.mAngle;
    }

    public Angle mul(double val) {
        return new Angle(mAngle * val);
    }

    public void setAngle(double val) {
        mAngle = val;
    }

    public static double normalize(double angle)
    {
        // TODO: See if modulo would be enough
        if (angle > 0) {
            while (angle >= TWO_PI)
                angle -= TWO_PI;
        } else {
            while (angle < 0)
                angle += TWO_PI;
        }
        return angle;
    }
    /**
     * Bring the value of mAngle between 0 and 2*PI.
     */
    public void normalize() {
        mAngle = Angle.normalize(mAngle);
    }

    public double getRawAngle() {
        normalize();
        return mAngle;
    }

    public double sin() {
        return Math.sin(mAngle);
    }

    public double cos() {
        return Math.cos(mAngle);
    }

    public double tan() { return Math.tan(mAngle); }

    @Override
    public int compareTo(@NonNull Angle another) {
        this.normalize();
        another.normalize();
        if (mAngle == another.mAngle)
            return 0;
        else
            return this.mAngle > another.mAngle ? 1 : -1;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%.3f", mAngle);
    }

    /**
     * Compare the Angle with another. However, should not be used, since _exact_ equality between
     * double cannot be trusted.
     * FIXME: We should compare the difference to a delta. Not important since this function is used only for testing, in
     * a controlled environment.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */

    @Override
    public boolean equals(Object another) {
        boolean ret = false;
        if (another.getClass() == this.getClass()) {
            ret = this.getRawAngle() == ((Angle) another).getRawAngle();
        }
        return ret;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(mAngle).hashCode();
    }

}
