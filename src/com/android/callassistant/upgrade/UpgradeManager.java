package com.android.callassistant.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.callassistant.R;
import com.android.callassistant.util.Log;
import com.google.gson.Gson;

public class UpgradeManager implements Runnable, OnClickListener {

    private static final String CONFIG_PATH = "https://github.com/taugin/callassistant/tree/master/release_version/config.json";

    private static final int ACTION_FETCH_CONFIG = 0;
    private static final int ACTION_DOWNLOAD = 1;

    private static UpgradeManager sUpgradeManager = null;
    private Context mContext;
    private int mAction = -1;

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
            URLConnection conn = url.openConnection();
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
            Log.d(Log.TAG, "config = " + builder.toString());
            return builder.toString();
        } catch (MalformedURLException e) {
            Log.d(Log.TAG, "error : " + e);
        } catch (IOException e) {
            Log.d(Log.TAG, "error : " + e);
        }
        return null;
    }

    private void upgradeCheck() {
        String config = getUpgradeConfig();
        if (TextUtils.isEmpty(config)) {
            return;
        }
        Gson gson = new Gson();
        UpgradeInfo info = gson.fromJson(config, UpgradeInfo.class);
        Log.d(Log.TAG, info.toString());
        int versionCode = getAppVer();
        if (versionCode >= info.version_code) {
            Toast.makeText(mContext, R.string.no_newversion_tip,
                    Toast.LENGTH_LONG).show();
            return;
        }

        newVersionDialog();
    }

    private void newVersionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.new_version_tiptitle);
        builder.setMessage(R.string.new_version_tipcontent);
        builder.create().show();
    }

    public void checkUpgrade() {
        mAction = ACTION_FETCH_CONFIG;
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
        // TODO Auto-generated method stub

    }
}
