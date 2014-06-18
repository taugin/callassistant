package com.android.phonerecorder.service;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

import com.android.phonerecorder.util.Constant;

public class RecordFileManager {

    private static RecordFileManager sRecordFileManager = null;
    private Context mContext;
    
    public static RecordFileManager getInstance(Context context) {
        if (sRecordFileManager == null) {
            sRecordFileManager = new RecordFileManager(context);
        }
        return sRecordFileManager;
    }
    private RecordFileManager(Context context) {
        mContext = context;
    }
    public String [] listRecordFiles() {
        File recordDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER);
        if (!recordDir.exists()) {
            return null;
        }
        
        return recordDir.list();
    }
    
    public String getProperName(String phoneNumber, boolean incomingFlag) {
        Calendar calendar = Calendar.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR));
        builder.append("-");
        builder.append(calendar.get(Calendar.MONTH) + 1);
        builder.append("-");
        builder.append(calendar.get(Calendar.DATE));
        builder.append("-");
        builder.append(calendar.get(Calendar.HOUR_OF_DAY));
        builder.append("-");
        builder.append(calendar.get(Calendar.MINUTE));
        builder.append("-");
        builder.append(calendar.get(Calendar.SECOND));;
        String incoming = incomingFlag ? "in" : "out";
        String fileName = "recorder_" + builder.toString() + "_" + incoming + "_" + phoneNumber + ".amr";
        return Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER + "/" + fileName;
    }
}
