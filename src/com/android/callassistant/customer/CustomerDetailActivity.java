package com.android.callassistant.customer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.android.callassistant.R;
import com.android.callassistant.info.ContactInfo;
import com.android.callassistant.manager.RecordFileManager;
import com.android.callassistant.provider.DBConstant;
import com.android.callassistant.util.Log;

public class CustomerDetailActivity extends Activity {

    private int mCurrentId = -1;
    
    private ArrayList<ContactInfo> mRecordList;
    private MyAdapter mAdapter;

    private ViewPager mPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_fragment);
        mRecordList = new ArrayList<ContactInfo>();
        mRecordList = RecordFileManager.getInstance(this).getContactFromDB(mRecordList);
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentId = intent.getIntExtra(DBConstant._ID, -1);
        }
        Log.d(Log.TAG, "CustomerDetailActivity onCreate");
        int pos = findPosFromId();
        mPager = (ViewPager)findViewById(R.id.view_pager);
        mAdapter = new MyAdapter(getFragmentManager());
        mPager.setAdapter(mAdapter);
        if (pos != -1) {
            mPager.setCurrentItem(pos);
        }
        //mPager.setOnPageChangeListener(this);
    }
    private int findPosFromId() {
        ContactInfo info = null;
        for (int index = 0; index < mRecordList.size(); index++) {
            info = mRecordList.get(index);
            if (info != null && info._id == mCurrentId) {
                return index;
            }
        }
        return -1;
    }
    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return mRecordList.size();
        }

        @Override
        public Fragment getItem(int position) {
            CustomerDetailFragment fragment = new CustomerDetailFragment();
            fragment.setContactId(mRecordList.get(position)._id);
            return fragment;
        }
    }
}
