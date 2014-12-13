package com.smfandroid.summitsaround;

import java.util.Collections;
import java.util.Vector;

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


public class ShowCameraView extends View implements CompassListener, GPSLocatorListener {
	private TextPaint mTextPaint, mDebugPaint;
	protected int counter = 0;
	private Paint mLinePaint;
	Compass m_compass;
	GPSLocator m_gps;
	float m_compassValues[];
	Angle m_horizontalAngle = Angle.A_ZERO;
	Angle m_horizontalViewAngle = Angle.A_HALF_PI;
	Location m_location = null;
	
	Vector<GUIPointOfInterest> m_pointsInterestList;
	
	void setHorizontalCameraAngle(Angle angle)
	{
		m_horizontalViewAngle = angle;
	}
	
	public ShowCameraView(Context context) {
		super(context);
		init(null, 0);
	}

	public ShowCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public ShowCameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
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

	void updatePointOfInterestList(Location loc)
	{
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
		
		for(GUIPointOfInterest p: m_pointsInterestList)
		{

			if(p.shouldDraw(m_horizontalViewAngle, m_horizontalAngle))
			{
				screenPosition = p.getPositionInGUI(m_horizontalViewAngle, getWidth(), getHeight(), m_horizontalAngle);
				
				float mod = index % 2;
				float positionY = 60 + (mod == 0 ? 30 : 0);
				canvas.drawText(p.getLabel(), screenPosition.x, positionY, mTextPaint);
				
				canvas.drawLine(screenPosition.x , positionY, screenPosition.x, screenPosition.y, mLinePaint);
			}
			index++;
		}
		
		canvas.drawText(String.valueOf(m_compassValues[0]), 0, getHeight() - 30, mDebugPaint);		
	}

	@Override
	public void sensorDataChanged(float[] values) {
		// TODO Evaluate frequency
		m_compassValues = values;
		m_horizontalAngle = new Angle(values[0]);
		ShowCameraView.this.postInvalidate();
	}

	@Override
	public void updatedPosition(Location loc, GPSAccuracy accuracy) {
		updatePointOfInterestList(loc);
	}


}

