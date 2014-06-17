package com.android.phonerecorder;

import com.android.phonerecorder.util.Constant;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneRecordService extends Service {

    private TelephonyManager mTelephonyManager;
    private RecordManager mRecordManager;
    private Handler mHandler;
    private boolean mIncomingFlag = false;
    private String mPhoneNumber = null;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mRecordManager = new RecordManager(this);
        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
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
        } else if (Constant.ACTION_OUTGOING_PHONE.equals(intent.getAction())) {
            mIncomingFlag = false;
        }
        mPhoneNumber = intent.getStringExtra(Constant.EXTRA_PHONE_NUMBER);
        logd((mIncomingFlag ? "Incoming PhoneNumber" : "Outgoing PhoneNumber") + " : " + mPhoneNumber);
        mRecordManager.setPhoneNumber(mPhoneNumber);
        mRecordManager.initRecorder();

        return super.onStartCommand(intent, flags, startId);
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String phoneNumber) {
            switch(state) {
            case TelephonyManager.CALL_STATE_IDLE:
                logd("CALL_STATE_IDLE recording = " + mRecordManager.recording());
                if (mRecordManager.recording()) {
                    mRecordManager.stopRecorder();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                logd("CALL_STATE_OFFHOOK recording = " + mRecordManager.recording());
                if (!mRecordManager.recording()) {
                    mRecordManager.startRecorder();
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                logd("CALL_STATE_RINGING phoneNumber = " + phoneNumber);
                break;
            default:
                break;
            }
        }
    };
    private void logd(String msg) {
        Log.d("taugin", msg);
    }
}
