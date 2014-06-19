package com.android.phonerecorder.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.phonerecorder.RecordInfo;
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
    public ArrayList<RecordInfo> listRecordFiles() {
        File recordDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER);
        if (!recordDir.exists()) {
            return null;
        }
        
        File files[] = recordDir.listFiles();
        if (files != null) {
            ArrayList<RecordInfo> list = new ArrayList<RecordInfo>();
            RecordInfo info = null;
            for (File file : files) {
                info = new RecordInfo();
                info.fileName = file.getName();
                info.displayName = getDisplayName(info.fileName);
                info.fileSize = file.length();
                info.fileCreateTime = getCreateTime(info.fileName);
                info.fileLastTime = file.lastModified();
                list.add(info);
            }
            return list;
        } else {
            return null;
        }
    }
    
    private String getDisplayName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        String []parts = fileName.split("_");
        if (parts == null || parts.length != 4) {
            return null;
        }
        return parts[0] + "_" + parts[3];
    }
    
    private long getCreateTime(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return 0;
        }
        String []parts = fileName.split("_");
        if (parts == null || parts.length != 4) {
            return 0;
        }
        if (TextUtils.isDigitsOnly(parts[1])) {
            return Long.parseLong(parts[1]);
        }
        return 0;
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
        // String fileName = "recorder_" + builder.toString() + "_" + incoming + "_" + phoneNumber + ".amr";
        String fileName = "recorder_" + calendar.getTimeInMillis() + "_" + incoming + "_" + phoneNumber + ".amr";
        return Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER + "/" + fileName;
    }
    
    public void deleteRecordFiles(ArrayList<RecordInfo> list) {
        String recordFile = null;
        int count = list.size();
        RecordInfo info = null;
        for (int index = count - 1; index >=0; index --) {
            info = list.get(index);
            if (info == null) {
                continue;
            }
            Log.d("taugin", "info = " + info.fileName);
            if (info.checked) {
                recordFile =  Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER + "/" + info.fileName;
                deleteRecordFile(recordFile);
                list.remove(info);
            }
        }
    }
    
    public boolean deleteRecordFile(String file) {
        try {
            File recordFile = new File(file);
            return recordFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getRecordFolder() {
        return Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER;
    }
}
