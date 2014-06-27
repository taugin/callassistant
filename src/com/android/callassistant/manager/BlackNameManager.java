package com.android.callassistant.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;

public class BlackNameManager {

    private static BlackNameManager sBlackNameManager = null;
    private Context mContext;
    public static BlackNameManager getInstance(Context context) {
        if (sBlackNameManager == null) {
            sBlackNameManager = new BlackNameManager(context);
        }
        return sBlackNameManager;
    }

    private BlackNameManager(Context context) {
        mContext = context;
    }

    public boolean isBlack(String phoneNumber) {
        if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("key_block_all", false)) {
            return true;
        }
        return false;
    }
    
    public boolean interceptPhoneNumber(String phoneNumber) {
        if (isBlack(phoneNumber)) {
            CallManager.getInstance(mContext).endCall();
            return true;
        }
        return false;
    }
}
