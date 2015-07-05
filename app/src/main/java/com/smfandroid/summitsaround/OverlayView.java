package com.smfandroid.summitsaround;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.Collections;
import java.util.Locale;
import java.util.Vector;


public class OverlayView extends View implements CompassListener, GPSLocatorListener {
    protected String TAG = getClass().getSimpleName();

    protected Compass m_compass;
    protected GPSLocator m_gps;
    protected PointManager m_pointManager;

    protected Angle m_deviceAzimuth = Angle.A_ZERO;
    protected Angle m_angleCorrection = Angle.A_ZERO;
    protected Angle m_horizontalViewAngle = Angle.A_HALF_PI;

    protected Location m_location = null;
    protected Vector<GUIPointOfInterest> m_pointsInterestList;

    protected Bitmap m_cameraBitmap;

    protected TextPaint m_textPaint, m_debugPaint;
    protected Paint m_linePaint;

    public OverlayView(Context context) {
        super(context);
    }
    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void setHorizontalCameraAngle(Angle angle) {
        m_horizontalViewAngle = angle;
    }

    public void init(PointManager pm) {

        m_pointManager = pm;
        m_compass = new Compass(getContext(), this);
        m_gps = new GPSLocator(getContext());

        // Set up a default TextPaint object
        m_textPaint = new TextPaint();
        m_textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        m_textPaint.setTextAlign(Paint.Align.CENTER);
        m_textPaint.setTextSize(50);
        m_textPaint.setTextSkewX(-0.25f);
        m_textPaint.setColor(Color.BLUE);
        m_textPaint.setShadowLayer(10f, 15f, 20f, Color.BLACK);
        m_textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        m_debugPaint = new TextPaint();
        m_debugPaint.setTextAlign(Paint.Align.LEFT);
        m_debugPaint.setTextSize(30);
        m_debugPaint.setTextSkewX(-0.25f);
        m_debugPaint.setColor(Color.BLUE);
        m_debugPaint.setTypeface(Typeface.DEFAULT_BOLD);

        m_linePaint = new Paint();
        m_linePaint.setColor(Color.RED);
        m_linePaint.setStyle(Style.STROKE);
        m_linePaint.setStrokeWidth(5);


        m_cameraBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera);

        this.setLayerType(LAYER_TYPE_HARDWARE, null);
        this.setBackgroundColor(Color.TRANSPARENT);

        m_gps.register(this);

        updatePointOfInterestList(null);
    }

    void updatePointOfInterestList(Location loc) {
        m_location = loc;
        m_pointsInterestList = m_pointManager.GetPointsForLocation(loc, true);
    }

    protected void setAngleCorrection(Angle correction)
    {
        Log.i(TAG, "New angle correction" + correction);
        m_angleCorrection = correction;
    }

    protected void drawAtAngle(Canvas canvas, Angle angle)
    {
        PointF screenPosition;

        int index = 0;

        Collections.sort(m_pointsInterestList);

        for (GUIPointOfInterest p : m_pointsInterestList) {
            if (p.shouldDraw(m_horizontalViewAngle, angle)) {
                screenPosition = p.getPositionInGUI(m_horizontalViewAngle, canvas.getWidth(), canvas.getHeight(), angle);
                float mod = index % 2;
                float positionY = 60 + (mod == 0 ? 50 : 0);
                canvas.drawText(p.getLabel(), screenPosition.x, positionY, m_textPaint);

                canvas.drawLine(screenPosition.x, positionY, screenPosition.x, screenPosition.y, m_linePaint);
            }
            index++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAtAngle(canvas, m_deviceAzimuth.sub(m_angleCorrection));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (prefs.getBoolean("camera_ready", false)) {
            canvas.drawBitmap(m_cameraBitmap, getWidth() / 2 - m_cameraBitmap.getWidth() / 2, (int) (getHeight() * 0.80), null);
        }

        drawDebugData(canvas);
    }

    private void drawDebugData(Canvas canvas) {
        float interline = -(m_debugPaint.ascent() + m_debugPaint.descent())*1.5f;
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
            canvas.drawText(line, 0, height, m_debugPaint);
            height -= interline;
        }
    }


    @Override
    public void updatedPosition(Location loc, GPSAccuracy accuracy) {
        updatePointOfInterestList(loc);
    }

    public void onPause()
    {
        m_gps.unregister();
    }

    public void onResume()
    {
        m_gps.register(this);
    }

    @Override
    public void sensorDataChanged(Angle azimuth, Angle pitch, Angle roll, int accuracy) {
        // TODO Evaluate frequency
        m_deviceAzimuth = azimuth;
        OverlayView.this.postInvalidate();
    }
}

