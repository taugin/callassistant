package com.android.phonerecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.android.phonerecorder.util.Constant;

public class PhoneRecorderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return ;
        }

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(Constant.ACTION_OUTGOING_PHONE);
            serviceIntent.putExtra(Constant.EXTRA_PHONE_NUMBER, getResultData());
            context.startService(serviceIntent);
        } else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent
                .getAction())) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                Intent serviceIntent = new Intent(Constant.ACTION_INCOMING_PHONE);
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                serviceIntent.putExtra(Constant.EXTRA_PHONE_NUMBER, incomingNumber);
                context.startService(serviceIntent);
            }
        }
    }

}
