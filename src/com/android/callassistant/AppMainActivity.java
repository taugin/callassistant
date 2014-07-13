package com.android.callassistant;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;

import com.android.callassistant.black.BlackListFragment;
import com.android.callassistant.customer.RecordListFragment;
import com.android.callassistant.util.FragmentListener;
import com.android.callassistant.util.Log;
import com.android.callassistant.view.TabContainer;
import com.android.callassistant.view.TabContainer.OnTabChangeListener;

public class AppMainActivity extends Activity implements OnPageChangeListener, TabListener, OnTabChangeListener {
    private static final int NUM_ITEMS = 3;
    private static final int RECORD_FRAGMENT = 0;
    private static final int BLACK_FRAGMENT = 1;
    private static final int OTHER_FRAGMENT = 2;

    private TabContainer mTabContainer;
    private RecordListFragment mRecordListFragment;
    private BlackListFragment mBlackListFragment;
    private RecordListFragment mOtherListFragment;
    private MyAdapter mAdapter;

    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mRecordListFragment = new RecordListFragment();
        mBlackListFragment = new BlackListFragment();
        mOtherListFragment = new RecordListFragment();
        mAdapter = new MyAdapter(getFragmentManager());

        mTabContainer = (TabContainer) findViewById(R.id.function_tab);
        mTabContainer.setOnTabChangeListener(this);
        mPager = (ViewPager)findViewById(R.id.view_pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);
        setTitle(R.string.call_log);
        
        /*
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tab tab1 = getActionBar().newTab().setText(R.string.call_log).setTabListener(this);
        Tab tab2 = getActionBar().newTab().setText(R.string.black_name).setTabListener(this);
        Tab tab3 = getActionBar().newTab().setText(R.string.call_log).setTabListener(this);
        getActionBar().addTab(tab1);
        getActionBar().addTab(tab2);
        getActionBar().addTab(tab3);
        */
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            //Log.d(Log.TAG, "getItem position = " + position);
            if (position == RECORD_FRAGMENT) {
                return mRecordListFragment;
            }
            if (position == BLACK_FRAGMENT) {
                return mBlackListFragment;
            }
            if (position == OTHER_FRAGMENT) {
                return mOtherListFragment;
            }
            return null;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        int position = mPager.getCurrentItem();
        Log.d(Log.TAG, "onPageScrollStateChanged state = " + state + " , position = " + position);
        FragmentListener mode = (FragmentListener) mAdapter.getItem(position);
        if (state == 1) {
            mode.finishActionModeIfNeed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Log.d(Log.TAG, "onPageScrolled position = " + position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(Log.TAG, "onPageSelected position = " + position);
        if (position == RECORD_FRAGMENT) {
            this.setTitle(R.string.call_log);
        }
        if (position == BLACK_FRAGMENT) {
            this.setTitle(R.string.black_name);
        }
        if (position == OTHER_FRAGMENT) {
            this.setTitle(R.string.call_log);
        }
        mTabContainer.setCurrentTab(position);
        /*
        Tab tab = getActionBar().getTabAt(position);
        getActionBar().selectTab(tab);
        */
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        mPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        
    }


    @Override
    public void onTabChange(int position) {
        mPager.setCurrentItem(position, false);
    }


    @Override
    public void onClick() {
        int position = mPager.getCurrentItem();
        FragmentListener mode = (FragmentListener) mAdapter.getItem(position);
        mode.finishActionModeIfNeed();
    }


    @Override
    public void onBackPressed() {
        int position = mPager.getCurrentItem();
        FragmentListener fragment = (FragmentListener) mAdapter.getItem(position);
        if (fragment.onBackPressed()) {
            return ;
        }
        super.onBackPressed();
    }
    
}
