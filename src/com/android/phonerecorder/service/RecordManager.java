package com.android.phonerecorder.service;

import java.io.File;
import java.io.IOException;

import com.android.phonerecorder.util.Constant;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class RecordManager {
    private MediaRecorder mMediaRecorder;
    private boolean mRecording;
    private boolean mIncomingFlag;
    private String mFileName = null;
    private int mCurDBId;
    
    private Context mContext;
    private static RecordManager sRecordManager;
    public static RecordManager getInstance(Context context) {
        if (sRecordManager == null) {
            sRecordManager = new RecordManager(context);
        }
        return sRecordManager;
    }
    private RecordManager(Context c) {
        mContext = c;
        mMediaRecorder = new MediaRecorder();
    }
    public synchronized void initRecorder(String fileName) {
        mFileName = fileName;
        mRecording = false;
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File recordDir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.FILE_RECORD_FOLDER);
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        mMediaRecorder.setOutputFile(fileName);
        logv("prepare recorder file : " + fileName);
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            logd(e.getLocalizedMessage());
            stopRecorder();
        } catch (IOException e) {
            logd(e.getLocalizedMessage());
            stopRecorder();
        }
    }
    public synchronized void startRecorder() {
        if (mMediaRecorder != null) {
            logv("startRecorder");
            mMediaRecorder.start();
            mRecording = true;
        }
    }
    
    public synchronized void stopRecorder() {
        if (mMediaRecorder != null) {
            logv("stopRecorder");
            mMediaRecorder.stop();
            mMediaRecorder.reset();
//            mMediaRecorder.release();
            mRecording = false;
        }
    }

    public void setDBId(int id) {
        mCurDBId = id;
    }

    public int getDBId() {
        return mCurDBId;
    }

    public String getFileName() {
        return mFileName;
    }

    public boolean recording() {
        return mRecording;
    }
    
    private void logd(String msg) {
        Log.d("taugin", msg);
    }
    private void logv(String msg) {
        Log.v("taugin", msg);
    }
    private void logw(String msg) {
        Log.w("taugin", msg);
    }
}
