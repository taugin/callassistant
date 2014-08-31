package com.android.callassistant;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());
        //new CrashHandler(this);
    }

}
