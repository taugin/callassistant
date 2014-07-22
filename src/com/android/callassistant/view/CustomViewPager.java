package com.android.callassistant.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.android.callassistant.AppMainActivity;

public class CustomViewPager extends ViewPager {

    private AppMainActivity mAppMainActivity;
    public CustomViewPager(Context context) {
        super(context, null);
    }
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMainActivity(AppMainActivity activity) {
        mAppMainActivity = activity;
    }
}
