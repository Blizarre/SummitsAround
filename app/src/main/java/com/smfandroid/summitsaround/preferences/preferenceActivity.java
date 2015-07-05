package com.smfandroid.summitsaround.preferences;

import android.app.Activity;
import android.os.Bundle;

import com.smfandroid.summitsaround.preferences.PrefsFragment;

/**
 * Created by clementdaviller on 16/05/15.
 */
public class preferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
}