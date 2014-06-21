package com.android.phonerecorder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.phonerecorder.BaseInfo;
import com.android.phonerecorder.RecordInfo;
import com.android.phonerecorder.provider.DBConstant;

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
    private ArrayList<RecordInfo> listRecordFiles(ArrayList<RecordInfo> list) {
        File recordDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER);
        if (!recordDir.exists()) {
            return null;
        }
        list.clear();

        File files[] = recordDir.listFiles();
        if (files != null) {
            RecordInfo info = null;
            for (File file : files) {
                info = new RecordInfo();
                info.recordFile = file.getName();
                info.recordName = getDisplayName(info.recordFile);
                info.recordSize = file.length();
                info.recordStart = getCreateTime(info.recordFile);
                info.incoming = incomingCall(info.recordFile);
                info.recordEnd = file.lastModified();
                list.add(info);
            }
            Collections.sort(list);
        }
        return list;
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

    private boolean incomingCall(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        String []parts = fileName.split("_");
        if (parts == null || parts.length != 4) {
            return false;
        }
        return "in".equals(parts[2]);
    }

    public String getProperName(String phoneNumber, long time) {
        String fileName = "recorder_" + time + "_" + phoneNumber + ".amr";
        return Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER + "/" + fileName;
    }
    
    private boolean deleteRecordFromDB(ArrayList<RecordInfo> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (RecordInfo info : list) {
            if (info.checked) {
                builder.append(info.recordId);
                builder.append(",");
            }
        }
        builder.append(")");
        builder.deleteCharAt(builder.length() - 2);
        String area = builder.toString();
        String where = DBConstant._ID + " IN " + area;
        Log.d("taugin", "where = " + where);
        int ret = mContext.getContentResolver().delete(DBConstant.RECORD_URI, where, null);
        Log.d("taugin", "deleteRecordFromDB ret = " + ret);
        return ret > 0;
    }
    public void deleteRecordFiles(ArrayList<RecordInfo> list) {
        String recordFile = null;
        int count = list.size();
        RecordInfo info = null;
        if (!deleteRecordFromDB(list)) {
            return ;
        }
        for (int index = count - 1; index >=0; index --) {
            info = list.get(index);
            if (info == null) {
                continue;
            }
            Log.d("taugin", "info = " + info.recordFile);
            if (info.checked) {
                recordFile =  Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER + "/" + info.recordFile;
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

    public void deleteBaseInfoFromDB(ArrayList<BaseInfo> list) {
        
    }

    public BaseInfo getSingleBaseInfo(int id) {
        Cursor c = null;
        BaseInfo info = null;
        try {
            Uri uri = ContentUris.withAppendedId(DBConstant.BASEINFO_URI, id);
            c = mContext.getContentResolver().query(uri, null, null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    info = new BaseInfo();
                    info._id = c.getInt(c.getColumnIndex(DBConstant._ID));
                    info.baseInfoName = c.getString(c.getColumnIndex(DBConstant.BASEINFO_NAME));
                    info.phoneNumber = c.getString(c.getColumnIndex(DBConstant.BASEINFO_NUMBER));
                    info.callLogCount = c.getInt(c.getColumnIndex(DBConstant.BASEINFO_CALL_LOG_COUNT));
                } 
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return info;
    }
    public ArrayList<BaseInfo> getBaseInfoFromDB(ArrayList<BaseInfo> list) {
        if (list == null) {
            return null;
        }
        list.clear();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DBConstant.BASEINFO_URI, null, null, null, DBConstant.BASEINFO_UPDATE + " DESC");
            if (c != null) {
                if (c.moveToFirst()) {
                    BaseInfo info = null;
                    do {
                        info = new BaseInfo();
                        info._id = c.getInt(c.getColumnIndex(DBConstant._ID));
                        info.baseInfoName = c.getString(c.getColumnIndex(DBConstant.BASEINFO_NAME));
                        info.phoneNumber = c.getString(c.getColumnIndex(DBConstant.BASEINFO_NUMBER));
                        info.callLogCount = c.getInt(c.getColumnIndex(DBConstant.BASEINFO_CALL_LOG_COUNT));
                        list.add(info);
                    } while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        //Collections.sort(list);
        return list;
    }
    public ArrayList<RecordInfo> getRecordsFromDB(ArrayList<RecordInfo> list, int id) {
        File recordDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER);
        if (!recordDir.exists()) {
            return list;
        }
        if (list == null) {
            return null;
        }
        list.clear();
        Cursor c = null;
        String selection = null;
        if (id != -1) {
            selection = DBConstant.RECORD_BASEINFO_ID + "=" + id;
        }
        Log.d("taugin", "getRecordsFromDB selection = " + selection);
        try {
            c = mContext.getContentResolver().query(DBConstant.RECORD_URI, null, selection, null, DBConstant.RECORD_START + " DESC");
            if (c != null) {
                if (c.moveToFirst()) {
                    RecordInfo info = null;
                    do {
                        info = new RecordInfo();
                        info.recordId = c.getInt(c.getColumnIndex(DBConstant._ID));
                        info.recordFile = c.getString(c.getColumnIndex(DBConstant.RECORD_FILE));
                        info.recordName = c.getString(c.getColumnIndex(DBConstant.RECORD_NAME));
                        info.recordSize = c.getLong(c.getColumnIndex(DBConstant.RECORD_SIZE));
                        info.recordStart = c.getLong(c.getColumnIndex(DBConstant.RECORD_START));
                        info.recordEnd = c.getLong(c.getColumnIndex(DBConstant.RECORD_END));
                        int flag = c.getInt(c.getColumnIndex(DBConstant.RECORD_FLAG));
                        info.incoming = flag == DBConstant.FLAG_INCOMING;
                        if (!recordExists(info.recordFile)) {
                            info.recordFile = null;
                        }
                        list.add(info);
                    } while(c.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        Collections.sort(list);
        return list;
    }

    private boolean recordExists(String recordFile) {
        if (recordFile == null) {
            return false;
        }
        File file = new File(recordFile);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private void deleteRecordByFile(String file) {
        String where = DBConstant.RECORD_FILE + "=" + "'" + file + "'";
        int ret = mContext.getContentResolver().delete(DBConstant.RECORD_URI, where, null);
        Log.d("taugin", "delete record by file : " + file + " , ret = " + ret);
    }
}
