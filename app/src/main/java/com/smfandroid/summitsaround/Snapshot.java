package com.smfandroid.summitsaround;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class Snapshot implements Camera.ShutterCallback, Camera.PictureCallback {
    protected String TAG = getClass().getSimpleName();

    ShowCameraView mCamView;
    CameraPreview mPrev;
    Canvas mCurrentCanvas;
    Angle mCurrentAngle;

    public Snapshot(ShowCameraView show, CameraPreview prev) {
        mCamView = show;
        mPrev = prev;
    }

    @Override
    public void onShutter() {
        Log.i(TAG, "onShutter called");
        mCurrentCanvas = new Canvas();
        mCurrentAngle = mCamView.m_deviceAzimuth;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
        Canvas tmpC = new Canvas(b);
        mCamView.drawAtAngle(tmpC, mCurrentAngle);

        Log.i(TAG, "onPictureTaken called");
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, "summitsAround.jpg");
        try {
            fOut = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
        } catch(IOException exc)
        {
            Toast.makeText(mCamView.getContext(), "Impossible to save image", Toast.LENGTH_LONG);
        }
        mPrev.onResume();
    }
}


