package com.android.phonerecorder;

import java.io.IOException;

import com.android.phonerecorder.service.RecordFileManager;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class RecordPlayer implements OnCompletionListener, OnErrorListener {

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private RecordInfo mCurRecordInfo;
    private OnCompletionListener mOnCompletionListener;
    
    private static RecordPlayer sRecordPlayer = null;
    
    public static RecordPlayer getInstance(Context context) {
        if (sRecordPlayer == null) {
            sRecordPlayer = new RecordPlayer(context);
        }
        return sRecordPlayer;
    }
    
    private RecordPlayer(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
    }
    
    public void prepareRecorder() {
        if (mCurRecordInfo == null) {
            return ;
        }
        try {
            String fileName = RecordFileManager.getInstance(mContext).getRecordFolder() + "/" + mCurRecordInfo.fileName;
            mMediaPlayer.setDataSource(fileName);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        if (mCurRecordInfo == null) {
            return ;
        }
        mCurRecordInfo.play = false;
        mCurRecordInfo = null;
        mMediaPlayer.reset();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion();
        }
    }
    
    public void setCurRecord(RecordInfo info) {
        mCurRecordInfo = info;
    }

    public RecordInfo getCurRecord() {
        return mCurRecordInfo;
    }

    public void stopPlay() {
        if (mCurRecordInfo == null) {
            return ;
        }
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mCurRecordInfo.play = false;
    }
    
    public void startPlay() {
        if (mCurRecordInfo == null) {
            return ;
        }
        prepareRecorder();
        mMediaPlayer.start();
        mCurRecordInfo.play = true;
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        if (mCurRecordInfo == null) {
            return false;
        }
        mCurRecordInfo.play = false;
        mCurRecordInfo = null;
        mMediaPlayer.reset();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion();
        }
        return false;
    }
    
    public boolean isPlaying() {
        if (mCurRecordInfo == null) {
            return false;
        }
        return mCurRecordInfo.play;
    }
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public interface OnCompletionListener {
        public void onCompletion();
    }
}
