package com.android.phonerecorder;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.phonerecorder.util.Constant;

public class PhoneRecordService extends Service {

    private static final boolean DEBUG = true;
    private static final int DELAY_TIME = 1000;
    private RecordManager mRecordManager;
    private boolean mIncomingFlag = false;
    private String mPhoneNumber = null;
    private Handler mHandler;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logv("onCreate");
        mRecordManager = new RecordManager(this);
        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        logv("onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        if (Constant.ACTION_INCOMING_PHONE.equals(intent.getAction())) {
            mIncomingFlag = true;
            mPhoneNumber = intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER);
            mHandler.postDelayed(mMonitorIncallScreen, DELAY_TIME);
        } else if (Constant.ACTION_OUTGOING_PHONE.equals(intent.getAction())) {
            mIncomingFlag = false;
            mPhoneNumber = intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER);
            mHandler.postDelayed(mMonitorIncallScreen, DELAY_TIME);
        } else if (Constant.ACTION_START_RECORDING.equals(intent.getAction())) {
            
        }
        logv((mIncomingFlag ? "Incoming PhoneNumber" : "Outgoing PhoneNumber") + " : " + mPhoneNumber);

        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable mMonitorIncallScreen = new Runnable() {
        @Override
        public void run() {
            if (!topIncallScreen()) {
                stopRecord();
                stopSelf();
                return ;
            }
            startRecord();
            mHandler.postDelayed(mMonitorIncallScreen, DELAY_TIME);
        }
    };

    private void startRecord() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK && !mRecordManager.recording()) {
            mRecordManager.initRecorder(mPhoneNumber);;
            mRecordManager.startRecorder();
        }
    }

    private void stopRecord() {
        if (mRecordManager.recording()) {
            mRecordManager.stopRecorder();
        }
    }

    private boolean topIncallScreen() {
        ActivityManager am = (ActivityManager) getSystemService(Service.ACTIVITY_SERVICE);
        if (am == null) {
            return false;
        }
        List<RunningTaskInfo> taskList = am.getRunningTasks(5);
        if (taskList == null) {
            return false;
        }
        if (taskList.get(0) == null) {
            return false;
        }
        ComponentName topActivity = taskList.get(0).topActivity;
        if (topActivity == null) {
            return false;
        }
        logd(topActivity.getClassName());
        return "com.android.phone.InCallScreen".equals(topActivity.getClassName());
    }
    private void logd(String msg) {
        if (DEBUG) {
            Log.d("taugin", msg);
        }
    }
    private void logv(String msg) {
        Log.v("taugin", msg);
    }
    private void logw(String msg) {
        Log.w("taugin", msg);
    }
}
