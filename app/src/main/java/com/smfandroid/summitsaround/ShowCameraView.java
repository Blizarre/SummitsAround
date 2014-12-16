package com.smfandroid.summitsaround;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.location.Location;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Collections;
import java.util.Locale;
import java.util.Vector;


public class ShowCameraView extends View implements CompassListener, GPSLocatorListener {
    protected int counter = 0;
    protected Compass m_compass;
    protected GPSLocator m_gps;
    protected Angle m_deviceAzimuth = Angle.A_ZERO;
    protected Angle m_horizontalViewAngle = Angle.A_HALF_PI;
    protected Location m_location = null;
    protected Vector<GUIPointOfInterest> m_pointsInterestList;
    protected TextPaint mTextPaint, mDebugPaint;
    protected Paint mLinePaint;

    public ShowCameraView(Context context) {
        super(context);
        init();
    }

    public ShowCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShowCameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void setHorizontalCameraAngle(Angle angle) {
        m_horizontalViewAngle = angle;
    }

    private void init() {
        m_compass = new Compass(getContext(), this);
        m_gps = new GPSLocator(getContext());

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(50);
        mTextPaint.setTextSkewX(-0.25f);
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setShadowLayer(10f, 15f, 20f, Color.BLACK);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mDebugPaint = new TextPaint();
        mDebugPaint.setTextAlign(Paint.Align.LEFT);
        mDebugPaint.setTextSize(30);
        mDebugPaint.setTextSkewX(-0.25f);
        mDebugPaint.setColor(Color.BLUE);
        mDebugPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStyle(Style.STROKE);
        mLinePaint.setStrokeWidth(5);

        this.setLayerType(LAYER_TYPE_HARDWARE, null);
        this.setBackgroundColor(Color.TRANSPARENT);

        m_gps.register(this);
        updatePointOfInterestList(null);
    }

    void updatePointOfInterestList(Location loc) {
        m_location = loc;
        m_pointsInterestList = new PointManager().GetPointsForLocation(loc, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PointF screenPosition;

        counter++;

        int index = 0;

        Collections.sort(m_pointsInterestList);

        for (GUIPointOfInterest p : m_pointsInterestList) {

            if (p.shouldDraw(m_horizontalViewAngle, m_deviceAzimuth)) {
                screenPosition = p.getPositionInGUI(m_horizontalViewAngle, getWidth(), getHeight(), m_deviceAzimuth);

                float mod = index % 2;
                float positionY = 60 + (mod == 0 ? 50 : 0);
                canvas.drawText(p.getLabel(), screenPosition.x, positionY, mTextPaint);

                canvas.drawLine(screenPosition.x, positionY, screenPosition.x, screenPosition.y, mLinePaint);
            }
            index++;
        }

        drawDebugData(canvas);
    }

    private void drawDebugData(Canvas canvas) {
        float interline = -(mDebugPaint.ascent() + mDebugPaint.descent())*1.5f;
        float height = getHeight() - interline;
        String debugData = SingletonDebugData.getInstance().toString();

        debugData += String.format("\nApp Compass: %02.3f\n", m_deviceAzimuth.getRawAngle());

        if(m_location == null)
        {
            debugData += "App Waiting for GPS . . .";
        }
        else
        {
            debugData += String.format(Locale.ENGLISH, "App GPS Location: %03.4f, %03.4f", m_location.getLatitude(), m_location.getLongitude());
        }

        for (String line: debugData.split("\n")) {
            canvas.drawText(line, 0, height, mDebugPaint);
            height -= interline;
        }
    }


    @Override
    public void updatedPosition(Location loc, GPSAccuracy accuracy) {
        updatePointOfInterestList(loc);
    }


    @Override
    public void sensorDataChanged(Angle azimuth, Angle pitch, Angle roll) {
        // TODO Evaluate frequency
        m_deviceAzimuth = azimuth;
        ShowCameraView.this.postInvalidate();

    }
}

