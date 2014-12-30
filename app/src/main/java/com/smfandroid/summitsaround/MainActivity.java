package com.smfandroid.summitsaround;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.InputStreamReader;

public class MainActivity extends Activity {
    CameraPreview mPreview = null;
    PointManager mPointManager;

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

            try {
                InputStreamReader dataFile = FileSystemFileReader.openFile("summitsAround.json");
                mPointManager.loadFromJson(dataFile);
            }
            catch (Exception e) {
                Toast.makeText(this, "Error loading json data file: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                mPointManager.loadDefaultData();
            }

            animation.init(mPointManager);
            Angle cameraHorizontalViewAngle = new Angle(Math.toRadians(mPreview.getParameters().getHorizontalViewAngle()));
            animation.setHorizontalCameraAngle(cameraHorizontalViewAngle);
            animation.bringToFront();
        } else {
            Toast.makeText(getBaseContext(), "Error: Camera not available", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}
