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
    
    private Context mContext;
    public RecordManager(Context c) {
        mContext = c;
        
    }
    
    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }
    public void initRecorder() {
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
        Log.d("taugin", "fileName = " + fileName);
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
    public void startRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.start();
        }
    }
    
    public void stopRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
}
