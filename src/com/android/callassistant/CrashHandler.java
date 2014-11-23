package com.android.callassistant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.android.callassistant.util.Log;

/**
 * 全局处理异常.
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
    /** CrashHandler实例 */

    private static CrashHandler instance;
    private UncaughtExceptionHandler mDefaultExceptionHandler;
    private App mApp;

    /** 获取CrashHandler实例 ,单例模式*/

    public static CrashHandler getInstance(App app) {
        if (instance == null) {
            instance = new CrashHandler(app);
        }
        return instance;
    }

    private CrashHandler(App app) {
        mApp = app;
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        final String logFile = mApp.getLogFile();
        if (logFile != null) {
            saveCrashInfo2File(ex);
        }
        mDefaultExceptionHandler.uncaughtException(thread, ex);
    }

    private void writeExceptionToFile(Thread thread, Throwable ex,
            String logFile) {
        try {
            FileWriter fw = new FileWriter(logFile, true);
            String tag = "E/AndroidRuntime" + "(" + android.os.Process.myTid() + "): ";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fw.write(sdf.format(new Date()) + "\n");
            StackTraceElement[] stackTrace = ex.getStackTrace();
            fw.write(tag + ex.getMessage() + "\n");
            String fileName = null;
            String className = null;
            String methodName = null;
            int lineNumber = -1;
            String msg = "";
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
            Log.e(Log.TAG, "load file failed..." + e);
        }
    }
    private void saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        try {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    final String logFile = mApp.getLogFile();
                    File dir = new File(logFile);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    Log.d(Log.TAG, "path = " + (logFile));
                    FileOutputStream fos = new FileOutputStream(logFile);
                    fos.write(sb.toString().getBytes());
                    fos.close();
                }
            } catch (Exception e) {
                Log.e(Log.TAG, "load file failed..." + e);
            }
        } catch (OutOfMemoryError e) {
            Log.e(Log.TAG, "load file failed..." + e);
        }
    }
    private void printExceptionToFile(Thread thread, Throwable ex,
            String logFile) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.d(Log.TAG, "fileName = " + logFile);
            FileWriter fw = new FileWriter(logFile, true);
            fw.write("Exception occured time : ");
            fw.write(sdf.format(new Date()) + "\n");
            PrintWriter err = new PrintWriter(fw, true);
            ex.printStackTrace(err);
            fw.write("==========================================================================================\n");
            err.close();
        } catch (FileNotFoundException e) {
            Log.e(Log.TAG, "error : " + e);
        } catch (IOException e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }
}

