package com.android.callassistant.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.android.callassistant.R;
import com.android.callassistant.manager.BlackNameManager;
import com.android.callassistant.manager.CallManager;
import com.android.callassistant.manager.RecordManager;
import com.android.callassistant.sersor.FlipManager;
import com.android.callassistant.util.Constant;
import com.android.callassistant.util.Log;
import com.android.callassistant.util.RecordFileManager;
import com.android.callassistant.util.ServiceUtil;

import java.util.List;

public class CallAssistantService extends Service {

    private static final boolean DEBUG = true;
    private static final int DELAY_TIME = 10 * 1000;
    private RecordManager mRecordManager;
    private CallFlag mCallFlag = CallFlag.UNDEFIED;
    private String mPhoneNumber = null;
    private Handler mHandler;
    private CallLogObserver mCallLogObserver;
    private TelephonyManager mTelephonyManager;
    private int mLastCallState = TelephonyManager.CALL_STATE_IDLE;

    public enum CallFlag {
        INCOMING,
        OUTGOING,
        UNDEFIED
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logv("onCreate");
        mRecordManager = RecordManager.getInstance(this);
        mHandler = new Handler();
        mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        //mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        logv("onDestroy");
        //mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return START_STICKY;
        }
        if (Constant.ACTION_INCOMING_PHONE.equals(intent.getAction())) {
            mCallFlag = CallFlag.INCOMING;
            mPhoneNumber = intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER);
            int state = intent.getIntExtra(Constant.EXTRA_PHONE_STATE, TelephonyManager.CALL_STATE_IDLE);
            if (BlackNameManager.getInstance(getBaseContext()).interceptPhoneNumber(mPhoneNumber)) {
                Log.getLog(getBaseContext()).recordOperation("Block a call : " + mPhoneNumber);
                return START_STICKY;
            }
            onCallStateChanged(state);

            if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("key_flip_mute", true)){
                FlipManager.getInstance(getBaseContext()).registerAccelerometerListener();
            }
            logv("onStartCommand Incoming PhoneNumber" + " : " + mPhoneNumber);
            Log.getLog(getBaseContext()).recordOperation("Incoming call : " + mPhoneNumber);
        } else if (Constant.ACTION_OUTGOING_PHONE.equals(intent.getAction())) {
            mCallFlag = CallFlag.OUTGOING;
            mPhoneNumber = intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER);
            //mHandler.postDelayed(mMonitorIncallScreen, DELAY_TIME);
            //mHandler.postDelayed(mEndCall, DELAY_TIME);
            logv("onStartCommand Outgoing PhoneNumber" + " : " + mPhoneNumber);
            if (mCallFlag == CallFlag.OUTGOING) {
                startRecord();
            }
            Log.getLog(getBaseContext()).recordOperation("Outgoing call : " + mPhoneNumber);
        } else if (Constant.ACTION_PHONE_STATE.equals(intent.getAction())) {
            int state = intent.getIntExtra(Constant.EXTRA_PHONE_STATE, TelephonyManager.CALL_STATE_IDLE);
            Log.d(Log.TAG, "onStartCommand state = " + stateToString(state));
            onCallStateChanged(state);
        }
        return START_STICKY;
    }

    private void onCallStateChanged(int state) {
        switch(state) {
        case TelephonyManager.CALL_STATE_IDLE:
            logv("onCallStateChanged mLastCallState = " + stateToString(mLastCallState) + " , state = [CALL_STATE_IDLE]" + " , CallFlag = " + mCallFlag.name());
            if (mLastCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
                stopRecord();
            }
            if (mCallFlag == CallFlag.INCOMING) {
                if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("key_flip_mute", true)){
                    FlipManager.getInstance(getBaseContext()).unregisterAccelerometerListener();
                }
            }
            String operation = null;
            if (mCallFlag == CallFlag.INCOMING) {
                operation = "Incoming phoneNumber : " + mPhoneNumber + " idle";
             } else if (mCallFlag == CallFlag.OUTGOING) {
                 operation = "Outgoing phoneNumber : " + mPhoneNumber + " idle";
            }

            Log.getLog(getBaseContext()).recordOperation(operation);
            mCallFlag = CallFlag.UNDEFIED;
            break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
            logv("onCallStateChanged mLastCallState = " + stateToString(mLastCallState) + " , state = [CALL_STATE_OFFHOOK]" + " , CallFlag = " + mCallFlag.name());
            if (mCallFlag == CallFlag.INCOMING) {
                startRecord();
            }

            operation = null;
            if (mCallFlag == CallFlag.INCOMING) {
                operation = "Incoming phoneNumber : " + mPhoneNumber + " offhook";
             } else if (mCallFlag == CallFlag.OUTGOING) {
                 operation = "Outgoing phoneNumber : " + mPhoneNumber + " offhook";
            }
            Log.getLog(getBaseContext()).recordOperation(operation);
            break;
        case TelephonyManager.CALL_STATE_RINGING:
            logv("onCallStateChanged mLastCallState = " + stateToString(mLastCallState) + " , state = [CALL_STATE_RINGING]" + " , CallFlag = " + mCallFlag.name());
            break;
        default:
            break;
        }
        mLastCallState = state;
    }

    private Runnable mEndCall = new Runnable() {
        public void run() {
            Log.d(Log.TAG, "mEndCall run mPhoneNumber = " + mPhoneNumber);
            if (mPhoneNumber.equals("1008611")) {
                CallManager.getInstance(CallAssistantService.this).endCall();
            }
        }
    };
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
        ensureRecordManager();
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        if (!mRecordManager.recording()) {
            long time = System.currentTimeMillis();
            int baseInfoId = ServiceUtil.addOrThrowBaseInfo(this, mPhoneNumber, time);
            String fileName = RecordFileManager.getInstance(CallAssistantService.this).getProperName(mPhoneNumber, time);
            int id = ServiceUtil.addNewRecord(this, baseInfoId, needRecord() ? fileName : null, time, mCallFlag, mPhoneNumber);
            mRecordManager.setDBId(id);
            if (needRecord()) {
                mRecordManager.initRecorder(fileName);
                mRecordManager.startRecorder();
                showNotification();
            }
        }
    }

    private boolean needRecord() {
        String recordContent = PreferenceManager.getDefaultSharedPreferences(this).getString("key_record_content", "all");
        if ("all".equals(recordContent)) {
            return true;
        } else if ("incoming".equals(recordContent) && mCallFlag == CallFlag.INCOMING) {
            return true;
        } else if ("outgoing".equals(recordContent) && mCallFlag == CallFlag.OUTGOING) {
            return true;
        }
        return false;
    }

    private void stopRecord() {
        ensureRecordManager();
        String fileName = mRecordManager.getFileName();
        ServiceUtil.updateRecord(this, mRecordManager.getDBId(), fileName);
        if (needRecord()) {
            if (mRecordManager.recording()) {
                mRecordManager.stopRecorder();
                Log.getLog(getBaseContext()).recordOperation("Saved record file " + fileName);
                cancel();
            }
        }
    }

    private void ensureRecordManager() {
        if (mRecordManager == null) {
            mRecordManager = RecordManager.getInstance(this);
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
    /* PhoneStateListener
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
    };*/

    private void showNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
        builder.setSmallIcon(R.drawable.ic_recording);
        builder.setTicker(getResources().getString(R.string.recording));
        builder.setContentText(getResources().getString(R.string.recording));
        builder.setContentTitle(getResources().getString(R.string.app_name));

        Notification notification = builder.getNotification();
        NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        startForeground(123456, notification);
    }
    
    private void cancel() {
        NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        stopForeground(true);
    }

    private class CallLogObserver extends ContentObserver{
        public CallLogObserver() {
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(Log.TAG, "CallLogObserver uri = " + uri);
        }
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
            Log.d(Log.TAG, msg);
        }
    }
    private void logv(String msg) {
        Log.v(Log.TAG, msg);
    }
}
