package com.smfandroid.summitsaround;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;


public class FileSystemFileReader {
    /***
     * From:
     *   http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
     * Checks if external storage is available to at least read
     ***/
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static InputStreamReader openFile (final String name) throws FileNotFoundException {
        File directory = new File(Environment.getExternalStorageDirectory(), name);
        return new FileReader(directory);
    }
}
