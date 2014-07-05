package com.android.callassistant.customer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.callassistant.R;
import com.android.callassistant.info.RecordInfo;
import com.android.callassistant.manager.RecordPlayerManager;
import com.android.callassistant.manager.RecordPlayerManager.OnCompletionListener;
import com.android.callassistant.provider.DBConstant;
import com.android.callassistant.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CallLogListView extends LinearLayout implements OnCheckedChangeListener, OnClickListener, OnCompletionListener {

    private CheckBox mCheckBox;
    private LinearLayout mCallLogContainer;
    private ArrayList<ImageView> mImageList;
    private RecordPlayerManager mRecordPlayer;
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
        Log.d(Log.TAG, "onFinishInflate");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.calllog_control, this);
        mImageList = new ArrayList<ImageView>();
        mCallLogContainer = (LinearLayout) view.findViewById(R.id.calllog_container);
        mCheckBox = (CheckBox) view.findViewById(R.id.call_log_show_control);
        mCheckBox.setOnCheckedChangeListener(this);
        mCheckBox.setChecked(true);
        mCallLogContainer.setVisibility(mCheckBox.isChecked() ? View.VISIBLE : View.GONE);
        mRecordPlayer = RecordPlayerManager.getInstance(getContext());
        mRecordPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(Log.TAG, "onCheckedChanged isChecked = " + isChecked);
        mCallLogContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    public void setCallLogList(ArrayList<RecordInfo> list) {
        mCallLogContainer.removeAllViews();
        mImageList.clear();
        int index = 0;
        RecordInfo info = null;
        Log.d(Log.TAG, "setCallLogList size = " + list.size());
        while (index < list.size()) {
            info = list.get(index);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.call_log_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.call_log_flag);
            int resId = 0;
            if (info.callFlag == DBConstant.FLAG_INCOMING) {
                resId = R.drawable.ic_incoming;
            } else if (info.callFlag == DBConstant.FLAG_OUTGOING) {
                resId = R.drawable.ic_outgoing;
            } else if (info.callFlag == DBConstant.FLAG_MISSCALL){
                resId = R.drawable.ic_missed_call;
            } else {
                resId = R.drawable.ic_block_call;
            }
            imageView.setImageResource(resId);

            TextView call_date = (TextView) view.findViewById(R.id.call_date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long updateTime = 0;
            if (info.callFlag >= DBConstant.FLAG_OUTGOING) {
                updateTime = info.recordStart;
            } else {
                updateTime = info.recordRing;
            }
            call_date.setText(sdf.format(new Date(updateTime)));

            TextView call_duration = (TextView) view.findViewById(R.id.call_duration);
            call_duration.setText(getTimeExperence(info.recordEnd - info.recordStart));

            ImageView media_control = (ImageView) view.findViewById(R.id.media_control);
            media_control.setTag(info);
            media_control.setOnClickListener(this);
            if (info.recordFile == null) {
                media_control.setVisibility(View.INVISIBLE);
            } else {
                media_control.setVisibility(View.VISIBLE);
            }
            mImageList.add(media_control);
            mCallLogContainer.addView(view);
            index ++;
        }
    }
    private String getTimeExperence(long timeExperence) {
        int allsec = Math.round(timeExperence / (float)1000);
        int min = allsec / 60;
        int sec = allsec % 60;
        int hour = min / 60;
        if (hour > 0) {
            min = min % 60;
        }
        String sHour = hour > 0 ? hour + "h" : "";
        String sMin = min > 0 ? min + "m" : "";
        String sSec = sec + "s";
        return String.valueOf(sHour + sMin + sSec);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.media_control) {
            RecordInfo info = (RecordInfo) v.getTag();
            if (mRecordPlayer.isPlaying()) {
                mRecordPlayer.stopPlay();
                if (mRecordPlayer.getCurRecord() != info) {
                    mRecordPlayer.setCurRecord(info);
                    mRecordPlayer.startPlay();
                }
            } else {
                mRecordPlayer.setCurRecord(info);
                mRecordPlayer.startPlay();
            }
            resetControlState();
            Log.d(Log.TAG, "fileName = " + info.recordFile);
        }
    }
    private void resetControlState() {
        for (ImageView v : mImageList) {
            RecordInfo info = (RecordInfo) v.getTag();
            if (!info.play) {
                v.setImageResource(R.drawable.ic_play);
            } else {
                v.setImageResource(R.drawable.ic_stop);
            }
        }
    }
    public void onPause() {
        mRecordPlayer.stopPlay();
        resetControlState();
    }
    public void onDestroy() {
        mRecordPlayer.release();
    }
    @Override
    public void onCompletion() {
        resetControlState();
    }
}
