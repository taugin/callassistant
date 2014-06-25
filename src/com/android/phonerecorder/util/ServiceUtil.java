package com.android.phonerecorder.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.phonerecorder.provider.DBConstant;
import com.android.phonerecorder.service.AppPhoneService;

import java.io.File;

public class ServiceUtil {

    public static int addOrThrowBaseInfo(Context context, String phoneNumber, long time) {
        Cursor c = null;
        int _id = -1;
        int count = 0;
        String selection = DBConstant.BASEINFO_NUMBER + " LIKE '%" + phoneNumber + "'";
        try {
            c = context.getContentResolver().query(DBConstant.BASEINFO_URI, new String[]{DBConstant._ID, DBConstant.BASEINFO_CALL_LOG_COUNT}, selection, null, null);
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
            context.getContentResolver().update(ContentUris.withAppendedId(DBConstant.BASEINFO_URI, _id), values, null, null);
            return _id;
        }
        ContentValues values = new ContentValues();
        values.put(DBConstant.BASEINFO_NUMBER, phoneNumber);
        values.put(DBConstant.BASEINFO_CALL_LOG_COUNT, 1);
        values.put(DBConstant.BASEINFO_UPDATE, time);
        Uri contentUri = context.getContentResolver().insert(DBConstant.BASEINFO_URI, values);
        return (int) ContentUris.parseId(contentUri);
    }

    public static int addNewRecord(Context context, int baseInfoId, String fileName, long timeStart, AppPhoneService.CallFlag callFlag, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put(DBConstant.RECORD_BASEINFO_ID, baseInfoId);
        values.put(DBConstant.RECORD_NAME, "record_" + phoneNumber + ".amr");
        values.put(DBConstant.RECORD_FILE, fileName);
        values.put(DBConstant.RECORD_NUMBER, phoneNumber);
        values.put(DBConstant.RECORD_FLAG, callFlag.ordinal());
        values.put(DBConstant.RECORD_START, timeStart);
        values.put(DBConstant.RECORD_END, timeStart);
        Uri uri = context.getContentResolver().insert(DBConstant.RECORD_URI, values);
        return (int) ContentUris.parseId(uri);
    }
    public static void updateRecord(Context context, int id, String fileName) {
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
        int ret = context.getContentResolver().update(uri, values, null, null);
    }
}
