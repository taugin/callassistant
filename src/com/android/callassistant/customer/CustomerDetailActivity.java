package com.android.callassistant.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.callassistant.R;
import com.android.callassistant.provider.DBConstant;
import com.android.callassistant.util.Log;

public class CustomerDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_fragment);
        CustomerDetailFragment fragment = (CustomerDetailFragment) getFragmentManager().findFragmentByTag("CustomerDetailFragment");
        Intent intent = getIntent();
        if (intent != null) {
            int id = intent.getIntExtra(DBConstant._ID, -1);
            fragment.setContactId(id);
        }
        Log.d(Log.TAG, "CustomerDetailActivity onCreate");
    }
}
