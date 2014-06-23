package com.android.phonerecorder;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PhoneRecordSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
