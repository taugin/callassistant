package com.android.callassistant.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import com.android.callassistant.R;
import com.android.callassistant.util.Log;

public class SettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private final String ENABLE_SERVICE = "tel:**67*13800000000%23";
    private final String ENABLE_POWEROFF_SERVICE = "tel:**67*13810538911%23";
    private final String ENABLE_STOP_SERVICE = "tel:**67*13701110216%23";
    //private final String DISABLE_SERVICE = "tel:%23%2321%23";
    private final String DISABLE_SERVICE = "tel:%23%2367%23";
    
    private static final String KEY_WARNING_TONE = "key_warning_tone";
    private static final String KEY_FLIP_MUTE = "key_flip_mute";
    private static final String KEY_BLOCK_ALL = "key_block_all";
    private static final String KEY_RECORD_CONTENT = "key_record_content";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        findPreference(KEY_WARNING_TONE).setOnPreferenceChangeListener(this);
        findPreference(KEY_FLIP_MUTE).setOnPreferenceChangeListener(this);
        findPreference(KEY_BLOCK_ALL).setOnPreferenceChangeListener(this);
        findPreference(KEY_RECORD_CONTENT).setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListPreference preference = (ListPreference) findPreference(KEY_WARNING_TONE);
        int index = preference.findIndexOfValue(preference.getValue());
        if (index != -1) {
            preference.setSummary(preference.getEntries()[index]);
        }
        
        preference = (ListPreference) findPreference(KEY_RECORD_CONTENT);
        index = preference.findIndexOfValue(preference.getValue());
        if (index != -1) {
            preference.setSummary(preference.getEntries()[index]);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(KEY_WARNING_TONE)) {
            String value = (String) newValue;
            Log.d(Log.TAG, "value = " + value);
            ListPreference list = (ListPreference) preference;
            list.setValue(value);
            int index = list.findIndexOfValue(value);
            if (index != -1) {
                preference.setSummary(list.getEntries()[index]);
            }
            setCallForward(value);
            Log.getLog(getActivity()).recordOperation("Set ringtone tip to " + list.getEntries()[index]);
            return true;
        } else if (preference.getKey().equals(KEY_FLIP_MUTE)) {
            Boolean value = (Boolean) newValue;
            String operation = value ? "Open flip mute" : "Close flip mute";
            Log.getLog(getActivity()).recordOperation(operation);
            return true;
        } else if (preference.getKey().equals(KEY_BLOCK_ALL)) {
            Boolean value = (Boolean) newValue;
            String operation = value ? "Open block all calls" : "Close block all calls";
            Log.getLog(getActivity()).recordOperation(operation);
            return true;
        } else if (preference.getKey().equals(KEY_RECORD_CONTENT)) {
            String value = (String) newValue;
            Log.d(Log.TAG, "value = " + value);
            ListPreference list = (ListPreference) preference;
            list.setValue(value);
            int index = list.findIndexOfValue(value);
            if (index != -1) {
                preference.setSummary(list.getEntries()[index]);
            }
            setCallForward(value);
            Log.getLog(getActivity()).recordOperation("Set record content to " + list.getEntries()[index]);
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
