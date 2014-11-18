package com.example.summitsAround;

import java.util.Collections;
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
	MONUMENT
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
		if(angle < 0 || angle >= 360)
		{
			throw new IllegalArgumentException("Angle must be between 0 and 360°");
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
		data.add(new PointOfInterest(PointType.TOWN, "New York", 5000, 90f));
		data.add(new PointOfInterest(PointType.SUMMIT, "Mont blanc", 200, 270f));
		data.add(new PointOfInterest(PointType.TOWN, "Paris", 300, 0f));
		data.add(new PointOfInterest(PointType.MONUMENT, "Part Dieu", 1000, 180f));
		return data;
	}
}

/**
 * TODO: document your custom view class.
 */
public class ShowCameraView extends View {
	private TextPaint mTextPaint;
	protected int counter = 0;
	private Paint mLinePaint;

	Vector<PointOfInterest> pointsInterestList;
	
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

		// Set up a default TextPaint object
		mTextPaint = new TextPaint();
		mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(50);
		mTextPaint.setTextSkewX(-0.25f);
		mTextPaint.setColor(Color.BLUE);
		mTextPaint.setShadowLayer(10f, 15f, 20f, Color.BLACK);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		mLinePaint = new Paint();
		mLinePaint.setColor(Color.RED);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(5);
		
		this.setLayerType(LAYER_TYPE_HARDWARE, null);
		this.setBackgroundColor(Color.TRANSPARENT);
		
		pointsInterestList = new PointManager().GetPointsForLocation(null);
		
		Thread animator = new Thread() {
			public void run() {
				boolean run = true;
				while(run)
				{
					try {
						sleep(1000/24);
					} catch (InterruptedException e) {
						run = false;
					}
					ShowCameraView.this.postInvalidate();
				}
			}
		};
		
		animator.start();
	
	}

	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//canvas.drawColor(Color.CYAN);
		// TODO: consider storing these as member variables to reduce
		// allocations per draw cycle.
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();

		counter++;
		
		int index = 0;
		Collections.sort(pointsInterestList);
		for(PointOfInterest p: pointsInterestList)
		{
			float mod = index % 2;
			float positionY = paddingTop + 60 + (mod == 0 ? 30 : 0);
			canvas.drawText(p.getLabel(), paddingLeft + p.getAngle()*3, positionY, mTextPaint);
			canvas.drawLine(paddingLeft + p.getAngle()*3 , positionY, paddingLeft + p.getAngle()*3, getHeight(), mLinePaint);
			index++;
		}
		
		// Draw the text.
		
	}


}
