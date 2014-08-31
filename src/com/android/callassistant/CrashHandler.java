package com.android.callassistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

/**
 * 全局处理异常.
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
    /** CrashHandler实例 */

    private static CrashHandler instance;
    private UncaughtExceptionHandler mDefaultExceptionHandler;

    /** 获取CrashHandler实例 ,单例模式*/

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    private CrashHandler() {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String logdir;
        if (Environment.getExternalStorageDirectory() != null) {
            logdir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + ".callassistant"
                    + File.separator + "log";

            File file = new File(logdir);
            boolean mkSuccess;
            if (!file.isDirectory()) {
                mkSuccess = file.mkdirs();
                if (!mkSuccess) {
                    mkSuccess = file.mkdirs();
                }
            }
            try {
                FileWriter fw = new FileWriter(logdir + File.separator
                        + "error.log", true);
                String tag = "E/AndroidRuntime" + "(" + android.os.Process.myTid() + "): ";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:MM:ss");
                fw.write(sdf.format(new Date()) + "\n");
                StackTraceElement[] stackTrace = ex.getStackTrace();
                fw.write(tag + ex.getMessage() + "\n");
                String fileName = null;
                String className = null;
                String methodName = null;
                int lineNumber = -1;
                String msg = "";
                /*
                for (int i = 0; i < stackTrace.length; i++) {
                    msg = tag + "\t";
                    fileName = stackTrace[i].getFileName();
                    className = stackTrace[i].getClassName();
                    methodName = stackTrace[i].getMethodName();
                    lineNumber = stackTrace[i].getLineNumber();
                    msg += "at ";
                    msg += className;
                    msg += "." + methodName;
                    if (lineNumber < 0) {
                        msg += "(" + fileName + ")";
                    } else {
                        msg += "(" + fileName + ":" + lineNumber + ")";
                    }
                    fw.write(msg + "\n");
                }*/
                Throwable caused = ex.getCause();
                stackTrace = caused.getStackTrace();
                for (int i = 0; i < stackTrace.length; i++) {
                    msg = tag + "\t";
                    fileName = stackTrace[i].getFileName();
                    className = stackTrace[i].getClassName();
                    methodName = stackTrace[i].getMethodName();
                    lineNumber = stackTrace[i].getLineNumber();
                    msg += "at ";
                    msg += className;
                    msg += "." + methodName;
                    if (lineNumber < 0) {
                        msg += "(" + fileName + ")";
                    } else {
                        msg += "(" + fileName + ":" + lineNumber + ")";
                    }
                    fw.write(msg + "\n");
                }
                fw.write("\n");
                fw.close();
            } catch (IOException e) {
                Log.e("crash handler", "load file failed...", e.getCause());
            }
        }
        mDefaultExceptionHandler.uncaughtException(thread, ex);
    }
}

