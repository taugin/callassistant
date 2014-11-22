package com.android.callassistant.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.callassistant.R;
import com.android.callassistant.util.Log;
import com.google.gson.Gson;

public class UpgradeManager implements Runnable, OnClickListener {

    private static final String CONFIG_PATH = "https://raw.githubusercontent.com/taugin/callassistant/master/release_version/config.json";

    private static final int ACTION_FETCH_CONFIG = 0;
    private static final int ACTION_DOWNLOAD = 1;

    private static final int MSG_SHOW_PROGRESS_DIALOG = 0;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 1;
    private static final int MSG_SHOW_TOAST = 2;
    private static final int MSG_SHOW_NEWVERSION_DIALOG = 3;

    private static UpgradeManager sUpgradeManager = null;
    private Context mContext;
    private int mAction = -1;
    private ProgressDialog mProgressDialog = null;

    private UpgradeManager(Context context) {
        mContext = context;
    }

    public static UpgradeManager get(Context context) {
        if (sUpgradeManager == null) {
            sUpgradeManager = new UpgradeManager(context);
        }
        return sUpgradeManager;
    }

    public String getUpgradeConfig() {
        try {
            URL url = new URL(CONFIG_PATH);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.connect();
            InputStream inStream = conn.getInputStream();
            byte buf[] = new byte[1024];
            int read = 0;
            StringBuilder builder = new StringBuilder();
            while ((read = inStream.read(buf)) > 0) {
                builder.append(new String(buf, 0, read));
            }
            inStream.close();
            Log.d(Log.TAG, "config = \n" + builder.toString());
            return builder.toString();
        } catch (MalformedURLException e) {
            Log.d(Log.TAG, "error : " + e);
        } catch (IOException e) {
            Log.d(Log.TAG, "error : " + e);
        }
        return null;
    }

    private void upgradeCheck() {
        mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
        String config = getUpgradeConfig();
        if (TextUtils.isEmpty(config)) {
            return;
        }
        Gson gson = new Gson();
        UpgradeInfo info = gson.fromJson(config, UpgradeInfo.class);
        Log.d(Log.TAG, info.toString());
        int versionCode = getAppVer();
        if (versionCode >= info.version_code) {
            mHandler.sendEmptyMessage(MSG_SHOW_TOAST);
            return;
        }
    }

    private void newVersionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.new_version_tiptitle);
        builder.setMessage(R.string.new_version_tipcontent);
        builder.create().show();
    }

    public void checkUpgrade() {
        mAction = ACTION_FETCH_CONFIG;
        mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
        new Thread(this).start();
    }

    @Override
    public void run() {
        if (ACTION_FETCH_CONFIG == mAction) {
            upgradeCheck();
        } else {

        }
    }

    private int getAppVer() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            Log.e(Log.TAG, "error : " + e);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        return -1;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(mContext.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case MSG_SHOW_PROGRESS_DIALOG:
                if (mProgressDialog == null) {
                    String content = mContext.getResources().getString(
                            R.string.loading);
                    mProgressDialog = ProgressDialog.show(mContext, null,
                            content,
                            true, false);
                    Log.d(Log.TAG, "show progress dialog");
                }
                break;
            case MSG_DISMISS_PROGRESS_DIALOG:
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                break;
            case MSG_SHOW_NEWVERSION_DIALOG:
                newVersionDialog();
                break;
            case MSG_SHOW_TOAST:
                Toast.makeText(mContext, R.string.no_newversion_tip,
                        Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
}
