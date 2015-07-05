package com.smfandroid.summitsaround;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends Activity implements View.OnTouchListener{
    protected final String TAG = getClass().getSimpleName();

    protected CameraPreview mPreview = null;
    protected PointManager mPointManager;
    protected Snapshot mSnap;
    protected OverlayView mOverlayView;
    protected Angle mCameraHorizontalViewAngle;

    protected boolean mIsMultiTouch = false;
    protected float mRotCorrection = 0.0f;
    protected Point mLastPoint = null;

    class Point {
        private final float X;
        private final float Y;
        public Point(float x, float y){X = x; Y = y;}
        public float getX() {return X;}
        public float getY() {return Y;}
    }
    Point downPt;
    Point upPt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of Camera
        if (checkCameraHardware(getBaseContext())) {
            setContentView(R.layout.activity_main);

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this);
            mCameraHorizontalViewAngle = new Angle(Math.toRadians(mPreview.getParameters().getHorizontalViewAngle()));
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            mOverlayView = (OverlayView) findViewById(R.id.animation_view);
            mSnap = new Snapshot(mOverlayView, mPreview);
            mPointManager = new PointManager(this);
            mPointManager.setPrefs(PreferenceManager.getDefaultSharedPreferences(this));
            mPointManager.reset();
            mOverlayView.init(mPointManager);
            mOverlayView.setHorizontalCameraAngle(mCameraHorizontalViewAngle);
            mOverlayView.bringToFront();
        } else {
            Toast.makeText(getBaseContext(), "Error: Camera not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mOverlayView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // ugly, should check if the parameters have changed
        mPointManager.reset();
        mOverlayView.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                Log.i(TAG, "MotionEvent Action DOWN");
                mLastPoint = new Point(e.getX(), e.getY());
                return true;
            }
            /* Another finger is in contact with the screen, this mean that
               the user want to calibrate the compass */
            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.i(TAG, "MotionEvent Action POINTER_DOWN");
                mIsMultiTouch = true;
                mLastPoint = new Point(e.getX(), e.getY());
                return true;
            }
            // All fingers are out of the screen, if it wasn't a multitouch, check if it is a swipe
            case MotionEvent.ACTION_UP: {
                Log.i(TAG, "MotionEvent Action UP");
                if(!mIsMultiTouch) {
                    Point upPt = new Point(e.getX(), e.getY());
                    onFling(mLastPoint, upPt);
                }
                return true;
            }
            // if the user want to calibrate the compass : must be a multitouch situation
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "MotionEvent Action MOVE");
                if(mIsMultiTouch)
                {
                    mRotCorrection += ( e.getX() - mLastPoint.getX() ) * mCameraHorizontalViewAngle.getRawAngle() / mOverlayView.getWidth();
                    mLastPoint = new Point(e.getX(), e.getY());
                    SingletonDebugData.getInstance().angleCorrection = mRotCorrection;

                    mOverlayView.setAngleCorrection(new Angle(mRotCorrection));
                }
        }
        return true;
    }


    // Show the preference panel. onActivityResult is called when the user exit the panel
    public void startSettingsActivity() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PreferenceActivity.class);
        startActivity(intent);
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    private static final int SWIPE_THRESHOLD = 100;

    public void onFling(Point pt1, Point pt2) {
        float diffY = pt2.getY() - pt1.getY();
        float diffX = pt2.getX() - pt1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
            }
        } else if (Math.abs(diffX) < Math.abs(diffY)) {
            if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
            }
        }
        else {
            onTouch();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent m) {
        return true;
    }

    public void onTouch() {
        Log.d(TAG, "screen touched");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean("camera_ready", false)) {
            mPreview.takePhoto(mSnap);
        }
    }

    public void onSwipeRight() {
        Log.i(TAG, "Swiped right");
        startSettingsActivity();
    }

    public void onSwipeLeft() {
        Log.i(TAG, "Swiped left");
    }

    public void onSwipeTop() {
        Log.i(TAG, "Swiped up");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("camera_ready", true);
        editor.apply();
    }

    public void onSwipeBottom() {
        Log.i(TAG, "Swiped down");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("camera_ready", false);
        editor.apply();
    }

}
