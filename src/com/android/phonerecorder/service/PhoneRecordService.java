package com.android.phonerecorder.service;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.phonerecorder.R;
import com.android.phonerecorder.provider.DBConstant;
import com.android.phonerecorder.util.Constant;
import com.android.phonerecorder.util.RecordFileManager;

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
        mRecordManager = RecordManager.getInstance(this);
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

    private int addOrThrowBaseInfo(String phoneNumber, long time) {
        Cursor c = null;
        int _id = -1;
        int count = 0;
        String selection = DBConstant.BASEINFO_NUMBER + " LIKE '%" + phoneNumber + "'";
        try {
            c = this.getContentResolver().query(DBConstant.BASEINFO_URI, new String[]{DBConstant._ID, DBConstant.BASEINFO_CALL_LOG_COUNT}, selection, null, null);
            if (c != null && c.moveToFirst() && c.getCount() > 0) {
                _id = c.getInt(c.getColumnIndex(DBConstant._ID));
                count = c.getInt(c.getColumnIndex(DBConstant.BASEINFO_CALL_LOG_COUNT));
            }
        } catch (Exception e) {
            
        } finally {
            if (c != null) {
                c.close();
            }
        }
        Log.d("taugin", "addOrThrowBaseInfo = id = " + _id);
        if (_id != -1) {
            ContentValues values = new ContentValues();
            values.put(DBConstant.BASEINFO_CALL_LOG_COUNT, (count + 1));
            values.put(DBConstant.BASEINFO_UPDATE, time);
            getContentResolver().update(ContentUris.withAppendedId(DBConstant.BASEINFO_URI, _id), values, null, null);
            return _id;
        }
        ContentValues values = new ContentValues();
        values.put(DBConstant.BASEINFO_NUMBER, phoneNumber);
        values.put(DBConstant.BASEINFO_CALL_LOG_COUNT, 1);
        values.put(DBConstant.BASEINFO_UPDATE, time);
        Uri contentUri = getContentResolver().insert(DBConstant.BASEINFO_URI, values);
        return (int) ContentUris.parseId(contentUri);
    }
    private int addNewRecord(int baseInfoId, String fileName, long timeStart, boolean incoming, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put(DBConstant.RECORD_BASEINFO_ID, baseInfoId);
        values.put(DBConstant.RECORD_NAME, "record_" + phoneNumber + ".amr");
        values.put(DBConstant.RECORD_FILE, fileName);
        values.put(DBConstant.RECORD_NUMBER, phoneNumber);
        values.put(DBConstant.RECORD_FLAG, incoming ? DBConstant.FLAG_INCOMING : DBConstant.FLAG_OUTGOING);
        values.put(DBConstant.RECORD_START, timeStart);
        Uri uri = getContentResolver().insert(DBConstant.RECORD_URI, values);
        return (int) ContentUris.parseId(uri);
    }
    private void updateRecord(int id) {
        String fileName = mRecordManager.getFileName();
        long size = 0;
        if (fileName != null) {
            File file = new File(fileName);
            if (file.exists()) {
                size = file.length();
            }
        }
        ContentValues values = new ContentValues();
        values.put(DBConstant.RECORD_END, System.currentTimeMillis());
        values.put(DBConstant.RECORD_SIZE, size);
        Uri uri = ContentUris.withAppendedId(DBConstant.RECORD_URI, id);
        int ret = getContentResolver().update(uri, values, null, null);
    }
    private void startRecord() {
        boolean record = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_automatic_record", true);
        ensureRecordManager();
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK && !mRecordManager.recording()) {
            long time = System.currentTimeMillis();
            int baseInfoId = addOrThrowBaseInfo(mPhoneNumber, time);
            String fileName = RecordFileManager.getInstance(PhoneRecordService.this).getProperName(mPhoneNumber, time);
            int id = addNewRecord(baseInfoId, record ? fileName : null, time, mIncomingFlag, mPhoneNumber);
            mRecordManager.setDBId(id);
            if (record) {
                mRecordManager.initRecorder(fileName);
                mRecordManager.startRecorder();
                showNotification();
            }
        }
    }

    private void stopRecord() {
        boolean record = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("key_automatic_record", true);
        ensureRecordManager();
        updateRecord(mRecordManager.getDBId());
        if (record) {
            if (mRecordManager.recording()) {
                mRecordManager.stopRecorder();
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
        Notification.Builder builder = new Notification.Builder(this);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
        builder.setSmallIcon(R.drawable.ic_recording);
        builder.setTicker(getResources().getString(R.string.recording));
        builder.setContentText(getResources().getString(R.string.recording));
        builder.setContentTitle(getResources().getString(R.string.app_name));
        
        Notification notification = builder.getNotification();
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
