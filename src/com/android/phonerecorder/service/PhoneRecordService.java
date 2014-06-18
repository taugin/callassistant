package com.android.phonerecorder.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.phonerecorder.R;
import com.android.phonerecorder.R.drawable;
import com.android.phonerecorder.R.string;
import com.android.phonerecorder.util.Constant;

public class PhoneRecordService extends Service {

    private static final boolean DEBUG = true;
    private static final int DELAY_TIME = 1000;
    private RecordManager mRecordManager;
    private boolean mIncomingFlag = false;
    private String mPhoneNumber = null;
    private Handler mHandler;
    private TelephonyManager mTelephonyManager;
    private int mLastCallState = TelephonyManager.CALL_STATE_IDLE;

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
        mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        logv("onDestroy");
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
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
            //mHandler.postDelayed(mMonitorIncallScreen, DELAY_TIME);
        } else if (Constant.ACTION_OUTGOING_PHONE.equals(intent.getAction())) {
            mIncomingFlag = false;
            mPhoneNumber = intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER);
            //mHandler.postDelayed(mMonitorIncallScreen, DELAY_TIME);
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
            TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            logv("call state = " + tm.getCallState());
            mHandler.postDelayed(mMonitorIncallScreen, DELAY_TIME);
        }
    };

    private void startRecord() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK && !mRecordManager.recording()) {
            mRecordManager.initRecorder(mPhoneNumber, mIncomingFlag);
            mRecordManager.startRecorder();
            showNotification();
        }
    }

    private void stopRecord() {
        if (mRecordManager.recording()) {
            mRecordManager.stopRecorder();
            cancel();
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

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch(state) {
            case TelephonyManager.CALL_STATE_IDLE:
                logv("mLastCallState = " + stateToString(mLastCallState) + " , state = [CALL_STATE_IDLE]");
                if (mLastCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    stopRecord();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                logv("mLastCallState = " + stateToString(mLastCallState) + " , state = [CALL_STATE_OFFHOOK]");
                startRecord();
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                logv("mLastCallState = " + stateToString(mLastCallState) + " , state = [CALL_STATE_RINGING]");
                break;
            default:
                break;
            }
            mLastCallState = state;
        }
    };

    private void showNotification() {
        boolean show = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_show_notification", true);
        if (!show) {
            return ;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
        builder.setSmallIcon(R.drawable.ic_recording);
        builder.setTicker(getResources().getString(R.string.recording));
        builder.setContentText(getResources().getString(R.string.recording));
        builder.setContentTitle(getResources().getString(R.string.app_name));
        
        Notification notification = builder.build();
        NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        nm.notify(123456, notification);
    }
    
    private void cancel() {
        boolean show = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_show_notification", true);
        if (!show) {
            return ;
        }
        NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        nm.cancel(123456);
    }
    private String stateToString(int state) {
        switch(state) {
        case TelephonyManager.CALL_STATE_IDLE:
            return "[CALL_STATE_IDLE]";
        case TelephonyManager.CALL_STATE_OFFHOOK:
            return "[CALL_STATE_OFFHOOK]";
        case TelephonyManager.CALL_STATE_RINGING:
            return "[CALL_STATE_RINGING]";
        default:
            return "";
        }
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
