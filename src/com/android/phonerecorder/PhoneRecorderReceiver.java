package com.android.phonerecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneRecorderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        if (arg1 == null) {
            return ;
        }
        if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent = new Intent(arg0, PhoneRecordService.class);
            arg0.startService(intent);
        } else if (Intent.ACTION_NEW_OUTGOING_CALL.equals(arg1.getAction())) {
            Log.d("taugin", "dial number = " + getResultData());
            Intent intent = new Intent("com.android.phonerecorder.action.NEW_OUTGONG_CALL");
            intent.putExtra("OUTGONG_CALL_NUMBER", getResultData());
            arg0.startService(intent);
        }
    }

}
