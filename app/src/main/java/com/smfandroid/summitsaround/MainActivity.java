package com.smfandroid.summitsaround;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.content.Intent;

import java.io.InputStreamReader;

public class MainActivity extends Activity implements View.OnTouchListener {
    CameraPreview mPreview = null;
    PointManager mPointManager;

    class point{
        private float X;
        private float Y;
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
            ShowCameraView animation = (ShowCameraView) findViewById(R.id.animation_view);
            mPointManager = new PointManager();
            mPointManager.setPrefs(PreferenceManager.getDefaultSharedPreferences(this));
            mPointManager.reset();
            /*try {
                InputStreamReader dataFile = FileSystemFileReader.openFile("summits/summitsAround.json");
                mPointManager.loadFromJson(dataFile);
            }
            catch (Exception e) {
                //Toast.makeText(this, "Error loading json data file: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                mPointManager.loadDefaultData();
            }*/
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
        ShowCameraView animation = (ShowCameraView) findViewById(R.id.animation_view);
        animation.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowCameraView animation = (ShowCameraView) findViewById(R.id.animation_view);
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
        ShowCameraView animation = (ShowCameraView) findViewById(R.id.animation_view);
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
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;


    public boolean onDown(MotionEvent e) {
        return true;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        //onTouch(e);
        return true;
    }


    public boolean onFling(point pt1, point pt2) {
        boolean result = false;
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
        return result;
    }

    @Override
    public boolean onTouch(View v, MotionEvent m) {
        return true;
    }

    public boolean onTouch() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("camera_ready", false)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("camera_shoot", true);
            editor.commit();
        }
        return true;
    }

    public void onSwipeRight() {
        Toast T = Toast.makeText(this, "swiped right ", Toast.LENGTH_LONG);
        T.show();
        startSettingsActivity();
    }

    public void onSwipeLeft() {
        Toast T = Toast.makeText(this, "swiped left", Toast.LENGTH_LONG);
        T.show();
    }

    public void onSwipeTop() {
        //Toast T = Toast.makeText(this, "camera activated", Toast.LENGTH_LONG);
        //T.show();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("camera_ready", true);
        editor.commit();
    }

    public void onSwipeBottom() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("camera_ready", false);
        editor.commit();
    }

}
