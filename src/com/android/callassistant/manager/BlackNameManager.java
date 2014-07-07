package com.android.callassistant.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.callassistant.provider.DBConstant;

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
        return isBlackInDB(phoneNumber);
    }

    public boolean isBlackInDB(String number) {
        String where = DBConstant.BLOCK_NUMBER + " LIKE '%" + number + "'";
        Cursor c = null;
        int count = 0;
        try {
            c = mContext.getContentResolver().query(DBConstant.BLOCK_URI, null, where, null, null);
            if (c != null) {
                count = c.getCount();
            }
        } catch (Exception e) {
            
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count > 0;
    }
    public boolean interceptPhoneNumber(String phoneNumber) {
        if (isBlack(phoneNumber)) {
            Telephony.getInstance(mContext).endCall();
            updateBlock(phoneNumber);
            return true;
        }
        return false;
    }
    
    public void updateBlock(String phoneNumber) {
        String where = DBConstant.BLOCK_NUMBER + " LIKE '%" + phoneNumber + "'";
        int count = getBlockCount(phoneNumber);
        long time = System.currentTimeMillis();
        String dates = getBlockDates(phoneNumber);
        if (TextUtils.isEmpty(dates)) {
            dates = "";
            dates += String.valueOf(time);
        } else {
            dates += ",";
            dates += String.valueOf(time);
        }
        ContentValues values = new ContentValues();
        values.put(DBConstant.BLOCK_COUNT, count + 1);
        values.put(DBConstant.BLOCK_TIME, dates);
        values.put(DBConstant.BLOCK_TYPE, DBConstant.BLOCK_TYPE_CALL);
        mContext.getContentResolver().update(DBConstant.BLOCK_URI, values, where, null);
    }
    public int getBlockCount(String phoneNumber) {
        String where = DBConstant.BLOCK_NUMBER + " LIKE '%" + phoneNumber + "'";
        Cursor c = null;
        int count = 0;
        try {
            c = mContext.getContentResolver().query(DBConstant.BLOCK_URI, null, where, null, null);
            if (c != null && c.moveToFirst()) {
                count = c.getInt(c.getColumnIndex(DBConstant.BLOCK_COUNT));
            }
        } catch (Exception e) {
            
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }
    public boolean deleteBlackName(String phoneNumber) {
        String where = DBConstant.BLOCK_NUMBER + " LIKE '%" + phoneNumber + "'";
        return mContext.getContentResolver().delete(DBConstant.BLOCK_URI, where, null) > 0;
    }
    
    public String getBlockDates(String phoneNumber) {
        String where = DBConstant.BLOCK_NUMBER + " LIKE '%" + phoneNumber + "'";
        Cursor c = null;
        String dates = null;
        try {
            c = mContext.getContentResolver().query(DBConstant.BLOCK_URI, null, where, null, null);
            if (c != null && c.moveToFirst()) {
                dates = c.getString(c.getColumnIndex(DBConstant.BLOCK_TIME));
            }
        } catch (Exception e) {
            
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return dates;
    }
}
