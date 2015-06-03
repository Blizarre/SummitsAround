package com.smfandroid.summitsaround;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by clementdaviller on 11/01/15.
 */

public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.displaypreferences);
    }
}
