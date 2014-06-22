package com.android.phonerecorder.customer;

import com.android.phonerecorder.R;
import com.android.phonerecorder.R.layout;

import android.app.Activity;
import android.os.Bundle;

public class PhoneRecordList extends Activity {
    private RecordListFragment mFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = (RecordListFragment) getFragmentManager().findFragmentByTag("RecordListFragment");
    }

    @Override
    public void onBackPressed() {
        if (!mFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
