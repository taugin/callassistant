package com.android.phonerecorder.customer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.android.phonerecorder.R;

public class CallLogListView extends LinearLayout implements OnCheckedChangeListener {

    private CheckBox mCheckBox;
    private LinearLayout mCallLogContainer;
    public CallLogListView(Context context) {
        super(context, null);
    }
    public CallLogListView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public CallLogListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("taugin", "onFinishInflate");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.calllog_control, this);
        mCallLogContainer = (LinearLayout) view.findViewById(R.id.calllog_container);
        mCheckBox = (CheckBox) view.findViewById(R.id.call_log_show_control);
        mCheckBox.setOnCheckedChangeListener(this);
        setCallLogList();
        mCallLogContainer.setVisibility(mCheckBox.isChecked() ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d("taugin", "onCheckedChanged isChecked = " + isChecked);
        mCallLogContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    public void setCallLogList() {
        int index = 0;
        while (index < 20) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.call_log_item, null);
            mCallLogContainer.addView(view);
            index ++;
        }
    }
}
