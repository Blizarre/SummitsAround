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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends Activity implements View.OnTouchListener{
    protected final String TAG = getClass().getSimpleName();

    CameraPreview mPreview = null;
    PointManager mPointManager;
    Snapshot mSnap;


    class point{
        private final float X;
        private final float Y;
        public point(float x, float y){X = x; Y = y;}
        public float getX() {return X;}
        public float getY() {return Y;}
    }
    point downPt;
    point upPt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of Camera
        if (checkCameraHardware(getBaseContext())) {
            setContentView(R.layout.activity_main);

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            OverlayView animation = (OverlayView) findViewById(R.id.animation_view);
            mSnap = new Snapshot(animation, mPreview);
            mPointManager = new PointManager();
            mPointManager.setPrefs(PreferenceManager.getDefaultSharedPreferences(this));
            mPointManager.reset();
            animation.init(mPointManager);
            Angle cameraHorizontalViewAngle = new Angle(Math.toRadians(mPreview.getParameters().getHorizontalViewAngle()));
            animation.setHorizontalCameraAngle(cameraHorizontalViewAngle);
            animation.bringToFront();
        } else {
            Toast.makeText(getBaseContext(), "Error: Camera not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        OverlayView animation = (OverlayView) findViewById(R.id.animation_view);
        animation.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        OverlayView animation = (OverlayView) findViewById(R.id.animation_view);
        animation.onResume();
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
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downPt = new point(e.getX(), e.getY());
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upPt = new point(e.getX(), e.getY());
                onFling(downPt, upPt);
                return true;
            }
        }
        return true;
    }


    public void startSettingsActivity() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        //super.onActivityResult(requestCode, resultCode, data);
        OverlayView animation = (OverlayView) findViewById(R.id.animation_view);
        animation.onPause();
        mPointManager.reset();
        animation.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(this, SetPreferenceActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Toast.makeText(this, "summits activated: "  + prefs.getBoolean("sommets", false), Toast.LENGTH_LONG).show();
    }


    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    private static final int SWIPE_THRESHOLD = 100;

    public void onFling(point pt1, point pt2) {
        try {
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
        } catch (Exception exception) {
            exception.printStackTrace();
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
