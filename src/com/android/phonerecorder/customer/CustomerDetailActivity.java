package com.android.phonerecorder.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.phonerecorder.R;
import com.android.phonerecorder.provider.DBConstant;

public class CustomerDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_fragment);
        CustomerDetailFragment fragment = (CustomerDetailFragment) getFragmentManager().findFragmentByTag("CustomerDetailFragment");
        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra(DBConstant._ID, -1);
            fragment.setBaseInfoId(id);
        }
        Log.d("taugin", "CustomerDetailActivity onCreate");
    }
}
