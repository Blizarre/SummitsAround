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
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Collections;
import java.util.Locale;
import java.util.Vector;


public class ShowCameraView extends View implements CompassListener, GPSLocatorListener {
    protected String TAG = getClass().getSimpleName();

    protected int counter = 0;
    protected Compass m_compass;
    protected GPSLocator m_gps;
    protected Angle m_deviceAzimuth = Angle.A_ZERO;
    protected Angle m_horizontalViewAngle = Angle.A_HALF_PI;
    protected Location m_location = null;
    protected Vector<GUIPointOfInterest> m_pointsInterestList;
    protected TextPaint mTextPaint, mDebugPaint;
    protected Paint mLinePaint;
    protected PointManager mPointManager;
    protected Bitmap mCameraBitmap;

    public ShowCameraView(Context context) {
        super(context);
    }

    public ShowCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void setHorizontalCameraAngle(Angle angle) {
        m_horizontalViewAngle = angle;
    }

    public void init(PointManager pm) {

        mPointManager = pm;
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


        mCameraBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera);

        this.setLayerType(LAYER_TYPE_HARDWARE, null);
        this.setBackgroundColor(Color.TRANSPARENT);

        m_gps.register(this);

        updatePointOfInterestList(null);
    }

    void updatePointOfInterestList(Location loc) {
        m_location = loc;
        m_pointsInterestList = mPointManager.GetPointsForLocation(loc, true);
    }

    protected void drawAtAngle(Canvas canvas, Angle angle)
    {
        PointF screenPosition;

        counter++;

        int index = 0;

        Collections.sort(m_pointsInterestList);

        for (GUIPointOfInterest p : m_pointsInterestList) {
            if (p.shouldDraw(m_horizontalViewAngle, angle)) {
                screenPosition = p.getPositionInGUI(m_horizontalViewAngle, canvas.getWidth(), canvas.getHeight(), angle);
                float mod = index % 2;
                float positionY = 60 + (mod == 0 ? 50 : 0);
                canvas.drawText(p.getLabel(), screenPosition.x, positionY, mTextPaint);

                canvas.drawLine(screenPosition.x, positionY, screenPosition.x, screenPosition.y, mLinePaint);
            }
            index++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAtAngle(canvas, m_deviceAzimuth);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (prefs.getBoolean("camera_ready", false)) {
            canvas.drawBitmap(mCameraBitmap, getWidth() / 2 - mCameraBitmap.getWidth() / 2, (int) (getHeight() * 0.80), null);
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
        ShowCameraView.this.postInvalidate();
    }
}

