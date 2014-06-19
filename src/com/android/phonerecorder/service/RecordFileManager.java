package com.android.phonerecorder.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
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
        
        String files[] = recordDir.list();
        if (files != null) {
            ArrayList<RecordInfo> list = new ArrayList<RecordInfo>();
            RecordInfo info = null;
            for (String file : files) {
                info = new RecordInfo();
                info.fileName = file;
                list.add(info);
            }
            return list;
        } else {
            return null;
        }
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
    
    public void deleteRecordFiles(ArrayList<RecordInfo> list) {
        String recordFile = null;
        for (RecordInfo info : list) {
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
}
