package com.android.phonerecorder.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

public class CallManager {

    public static CallManager sCallManager = null;
    private Context mContext;
    
    public static CallManager getInstance(Context context) {
        if (sCallManager == null) {
            sCallManager = new CallManager(context);
        }
        return sCallManager;
    }
    private CallManager(Context context) {
        mContext = context;
    }

    public void endCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder)method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
