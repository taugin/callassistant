package com.android.callassistant;

import java.io.File;

import android.app.Application;
import android.os.Environment;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    public String getLogFile() {
        if (Environment.getExternalStorageDirectory() != null) {
            String logdir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + ".callassistant"
                    + File.separator + "log";
            File dir = new File(logdir);
            dir.mkdirs();
            String file = logdir + File.separator + "error.log";
            return file;
        }
        return null;
    }
}
