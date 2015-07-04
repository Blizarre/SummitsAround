package com.smfandroid.summitsaround;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.style.TtsSpan;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        File file = new File(path, "summitsAround" + currentDateandTime + ".jpg");

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
            addImageToGallery(mCamView.getContext(), file.getAbsolutePath(), "summitAround", "summitAround");
        } catch(IOException exc)
        {
            Toast.makeText(mCamView.getContext(), "Impossible to save image", Toast.LENGTH_LONG);
        }
        mPrev.onResume();
    }

    // from http://stackoverflow.com/questions/21759476/android-save-bitmap-to-gallery-time-created-wrong
    public static Uri addImageToGallery(Context context, String filepath, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filepath);

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}


