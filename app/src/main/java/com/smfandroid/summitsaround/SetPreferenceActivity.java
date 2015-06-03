package com.smfandroid.summitsaround;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by clementdaviller on 16/05/15.
 */
public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
}