package com.android.phonerecorder.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.android.phonerecorder.R;

public class SettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_WARNING_TONE = "key_warning_tone";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        ListPreference preference = (ListPreference) findPreference(KEY_WARNING_TONE);
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListPreference preference = (ListPreference) findPreference(KEY_WARNING_TONE);
        int index = preference.findIndexOfValue(preference.getValue());
        if (index != -1) {
            preference.setSummary(preference.getEntries()[index]);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(KEY_WARNING_TONE)) {
            String value = (String) newValue;
            Log.d("taugin", "value = " + value);
            ListPreference list = (ListPreference) preference;
            list.setValue(value);
            int index = list.findIndexOfValue(value);
            if (index != -1) {
                preference.setSummary(list.getEntries()[index]);
            }
            return true;
        }

        return false;
    }

}
