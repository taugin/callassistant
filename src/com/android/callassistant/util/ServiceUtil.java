package com.android.callassistant.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;

import com.android.callassistant.manager.TmpStorageManager;
import com.android.callassistant.provider.DBConstant;

public class ServiceUtil {

    public static int addOrThrowBaseInfo(Context context, String phoneNumber, long time) {
        Cursor c = null;
        int _id = -1;
        int count = 0;
        String selection = DBConstant.CONTACT_NUMBER + " LIKE '%" + phoneNumber + "'";
        try {
            c = context.getContentResolver().query(DBConstant.CONTACT_URI, new String[]{DBConstant._ID, DBConstant.CONTACT_CALL_LOG_COUNT}, selection, null, null);
            if (c != null && c.moveToFirst() && c.getCount() > 0) {
                _id = c.getInt(c.getColumnIndex(DBConstant._ID));
                count = c.getInt(c.getColumnIndex(DBConstant.CONTACT_CALL_LOG_COUNT));
            }
        } catch (Exception e) {
            
        } finally {
            if (c != null) {
                c.close();
            }
        }
        Log.d(Log.TAG, "addOrThrowBaseInfo = id = " + _id);
        String name = getBaseNameFromContact(context, phoneNumber);
        Log.d(Log.TAG, "name = " + name);
        if (_id != -1) {
            ContentValues values = new ContentValues();
            values.put(DBConstant.CONTACT_CALL_LOG_COUNT, (count + 1));
            values.put(DBConstant.CONTACT_UPDATE, time);
            if (!TextUtils.isEmpty(name)) {
                values.put(DBConstant.CONTACT_NAME, name);
                values.put(DBConstant.CONTACT_MODIFY_NAME, DBConstant.MODIFY_NAME_FORBID);
            }
            context.getContentResolver().update(ContentUris.withAppendedId(DBConstant.CONTACT_URI, _id), values, null, null);
            return _id;
        }
        ContentValues values = new ContentValues();
        values.put(DBConstant.CONTACT_NUMBER, phoneNumber);
        values.put(DBConstant.CONTACT_CALL_LOG_COUNT, 1);
        values.put(DBConstant.CONTACT_UPDATE, time);
        if (!TextUtils.isEmpty(name)) {
            values.put(DBConstant.CONTACT_NAME, name);
            values.put(DBConstant.CONTACT_MODIFY_NAME, DBConstant.MODIFY_NAME_FORBID);
        }
        Uri contentUri = context.getContentResolver().insert(DBConstant.CONTACT_URI, values);
        
        return (int) ContentUris.parseId(contentUri);
    }
    
    public static void moveTmpInfoToDB(Context context) {
        Log.getLog(context).recordOperation("moveTmpInfoToDB");
        String phoneNumber = TmpStorageManager.getPhoneNumber(context);
        int callFlag = TmpStorageManager.getCallFlag(context);
        long ringTime = TmpStorageManager.getRingTime(context);
        long startTime = TmpStorageManager.getStartTime(context);
        long endTime = TmpStorageManager.getEndTime(context);
        String recordName = TmpStorageManager.getRecordName(context);
        String recordFile = TmpStorageManager.getRecordFile(context);
        long fileSize = TmpStorageManager.getRecordSize(context);
        boolean callBlock = TmpStorageManager.callBlock(context);

        long updateTime = 0;
        if (callFlag == DBConstant.FLAG_INCOMING) {
            updateTime = ringTime;
        } else if (callFlag == DBConstant.FLAG_OUTGOING) {
            updateTime = startTime;
        }
        int _id = addOrThrowBaseInfo(context, phoneNumber, updateTime);

        if (callBlock) {
            callFlag = DBConstant.FLAG_BLOCKCALL;
        }
        if (!callBlock && startTime == 0) {
            callFlag = DBConstant.FLAG_MISSCALL;
        }

        ContentValues value = new ContentValues();
        value.put(DBConstant.RECORD_CONTACT_ID, _id);
        value.put(DBConstant.RECORD_NUMBER, phoneNumber);
        value.put(DBConstant.RECORD_NAME, recordName);
        value.put(DBConstant.RECORD_FILE, recordFile);
        value.put(DBConstant.RECORD_FLAG, callFlag);
        value.put(DBConstant.RECORD_SIZE, fileSize);
        value.put(DBConstant.RECORD_RING, ringTime);
        value.put(DBConstant.RECORD_START, startTime);
        value.put(DBConstant.RECORD_END, endTime);
        Uri ret = context.getContentResolver().insert(DBConstant.RECORD_URI, value);
        Log.d(Log.TAG, "ret = " + ret);
    }
    
    private static String getBaseNameFromContact(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, phoneNumber);
        Cursor c = null;
        try {
            c = context.getContentResolver().query(uri, new String[]{Contacts.DISPLAY_NAME}, null, null, null);
            if (c != null && c.moveToFirst()) {
                return c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME));
            }
        } catch (Exception e) {
            Log.d("taugin2", e.getLocalizedMessage());
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return null;
    }
}
