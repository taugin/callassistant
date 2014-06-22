package com.android.phonerecorder.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.android.phonerecorder.R;

public class SettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    // 占线时转移，提示所拨的号码为空号
    private final String ENABLE_SERVICE = "tel:**67*13800000000%23";
    // 占线时转移，提示所拨的号码为关机
    private final String ENABLE_POWEROFF_SERVICE = "tel:**67*13810538911%23";
    // 占线时转移，提示所拨的号码为停机
    private final String ENABLE_STOP_SERVICE = "tel:**67*13701110216%23";
    // 占线时转移
    //private final String DISABLE_SERVICE = "tel:%23%2321%23";
    private final String DISABLE_SERVICE = "tel:%23%2367%23";
    
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
            setCallForward(value);
            return true;
        }

        return false;
    }

    private void setCallForward(String value) {
        String forwordNumber = null;
        if ("empty".equals(value)) {
            forwordNumber = ENABLE_SERVICE;
        } else if ("stop".equals(value)) {
            forwordNumber = ENABLE_STOP_SERVICE;
        } else if ("shutdown".equals(value)) {
            forwordNumber = ENABLE_POWEROFF_SERVICE;
        } else if ("busy".equals(value)) {
            forwordNumber = DISABLE_SERVICE;
        } else {
            return ;
        }
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse(forwordNumber));
        startActivity(i);
    }

}
