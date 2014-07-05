package com.android.callassistant;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.android.callassistant.black.BlackListFragment;
import com.android.callassistant.customer.RecordListFragment;
import com.android.callassistant.util.Log;

public class AppMainActivity extends Activity implements OnPageChangeListener {
    private static final int NUM_ITEMS = 3;
    private static final int RECORD_FRAGMENT = 0;
    private static final int BLACK_FRAGMENT = 1;
    private static final int OTHER_FRAGMENT = 2;

    private MyAdapter mAdapter;

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAdapter = new MyAdapter(getFragmentManager());

        mPager = (ViewPager)findViewById(R.id.view_pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(Log.TAG, "getItem position = " + position);
            if (position == RECORD_FRAGMENT) {
                return new RecordListFragment();
            }
            if (position == BLACK_FRAGMENT) {
                return new BlackListFragment();
            }
            if (position == OTHER_FRAGMENT) {
                return new RecordListFragment();
            }
            return null;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(Log.TAG, "onPageScrollStateChanged state = " + state);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Log.d(Log.TAG, "onPageScrolled position = " + position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(Log.TAG, "onPageSelected position = " + position);
        if (position == RECORD_FRAGMENT) {
            this.setTitle(R.string.app_name);
        }
        if (position == BLACK_FRAGMENT) {
            this.setTitle(R.string.black_name);
        }
        if (position == OTHER_FRAGMENT) {
            this.setTitle(R.string.app_name);
        }
    }
}
