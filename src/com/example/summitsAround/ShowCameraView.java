package com.example.summitsAround;

import java.util.Collections;
import java.util.Queue;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

class Location
{
	double latitude;
	double lontitude;
}

enum PointType
{
	SUMMIT,
	TOWN,
	MONUMENT,
	NONE
}

class PointOfInterest implements Comparable<PointOfInterest>
{
	// Type of the PointOfInterest
	PointType mType;
	
	// Distance in km
	protected float mDistance;
	
	// In degrees:
	// Warning: angle >= 0 && angle < 360
	// 0° = North, 180° = South, 270° = East, 90° = West
	float mAngle; 
	
	// Short description
	String mLabel;
	
	public PointOfInterest(PointType type, String label, float distance, float angle) {
		mType = type;
		mDistance = distance;
		mLabel = label;
		if(angle < 0 || angle >= 2 * Math.PI)
		{
			throw new IllegalArgumentException("Angle must be between 0 and 2*Pi");
		}
		mAngle = angle;
	}
	
	public PointType getType()
	{
		return mType;
	}
	public float getAngle()
	{
		return mAngle;
	}
	
	public String getLabel()
	{
		return mLabel;
	}
	
	public float getDistance(boolean inMiles) {
		return inMiles?mDistance * 0.6214f:mDistance;
	}

	@Override
	public int compareTo(PointOfInterest another) {
		return (int)(this.getAngle() - another.getAngle());
	}
}

class PointManager
{
	Vector<PointOfInterest> GetPointsForLocation(Location location)
	{
		Vector<PointOfInterest> data = new Vector<PointOfInterest>();
/*		data.add(new PointOfInterest(PointType.TOWN, "New York", 5000, 90f));
		data.add(new PointOfInterest(PointType.SUMMIT, "Mont blanc", 200, 270f));
		data.add(new PointOfInterest(PointType.TOWN, "Paris", 300, 0f));
		data.add(new PointOfInterest(PointType.MONUMENT, "Part Dieu", 1000, 180f));*/
		data.add(new PointOfInterest(PointType.NONE, "North", 1000, 0));
		data.add(new PointOfInterest(PointType.NONE, "South", 1000,(float)Math.PI));
		data.add(new PointOfInterest(PointType.NONE, "West", 1000,(float)(Math.PI/2.0d) ) );
		data.add(new PointOfInterest(PointType.NONE, "East", 1000,(float)(3.0d * Math.PI/2.0d) ) );


		return data;
	}
}


/**
 * TODO: document your custom view class.
 */
public class ShowCameraView extends View implements CompassListener {
	private TextPaint mTextPaint, mDebugPaint;
	protected int counter = 0;
	private Paint mLinePaint;
	Compass m_compass;
	float m_compassValues[];
	float m_horizontalAngle = (float) (Math.PI / 2.0d);

	Vector<PointOfInterest> pointsInterestList;
	
	void setHorizontalCameraAngle(float angle)
	{
		m_horizontalAngle = angle;
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
		
		pointsInterestList = new PointManager().GetPointsForLocation(null);
	}

	
	float normalize(double a) { return normalize((float)a); }

	float normalize(float a)
	{
		if (a > 2 * Math.PI)
		{
			return (float)(a - 2 * Math.PI);
		}
		else if (a < 0)
		{
			return (float)(a + 2 * Math.PI);
		}
		else
			return a;
	}
	
	boolean shouldDraw(float angle)
	{
		if(angle > Math.PI)
			angle = (float)(2*Math.PI - angle);
		
		float angleMax = m_horizontalAngle / 2.0f;
		
		return angle > angleMax;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// TODO: consider storing these as member variables to reduce
		// allocations per draw cycle.
		int paddingTop = getPaddingTop();

		counter++;
				
		int index = 0;
		
		float widthNorm = getWidth() / (float)Math.sin(m_horizontalAngle);
		
		Collections.sort(pointsInterestList);
		for(PointOfInterest p: pointsInterestList)
		{
			// Corrector for the fact that we are pointing the device PI/2 from the screen
			float correctedAngle = normalize(m_compassValues[0] - p.getAngle() - Math.PI / 2.0f);
			if(shouldDraw(correctedAngle))
			{
				
				float posX = getWidth()/2.0f + (float)Math.sin(correctedAngle) * widthNorm; 
			
				float mod = index % 2;
				float positionY = paddingTop + 60 + (mod == 0 ? 30 : 0);
				canvas.drawText(p.getLabel() + " " + posX, posX, positionY, mTextPaint);
				canvas.drawLine(posX , positionY, posX, getHeight(), mLinePaint);
			}
			index++;
		}
		
		canvas.drawText(""+m_compassValues[0], 0, getHeight() - 30, mDebugPaint);
		
		// Draw the text.
		
	}

	@Override
	public void sensorDataChanged(float[] values) {
		// TODO Evaluate frequency
		m_compassValues = values;
		ShowCameraView.this.postInvalidate();
	}


}

