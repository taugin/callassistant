package com.android.phonerecorder;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class RecordManager {
    private MediaRecorder mMediaRecorder;
    private String mPhoneNumber;
    private boolean mRecording;
    
    private Context mContext;
    public RecordManager(Context c) {
        mContext = c;
        mRecording = false;
    }
    public synchronized void initRecorder(String phoneNumber) {
        mPhoneNumber = phoneNumber;
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File recordDir = new File(Environment.getExternalStorageDirectory() + "/" + "recorder");
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        String fileName = "recorder_" + System.currentTimeMillis() + "_" + mPhoneNumber + ".amr";
        mMediaRecorder.setOutputFile(recordDir.getAbsolutePath() + "/" + fileName);
        logv("prepare recorder file : " + fileName);
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            stopRecorder();
        } catch (IOException e) {
            e.printStackTrace();
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
            mMediaRecorder.release();
            mRecording = false;
            mMediaRecorder = null;
        }
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
