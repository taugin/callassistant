package com.android.internal.telephony;

import android.os.Handler;

import com.android.callassistant.util.Log;

public class CallManager {
    
    public static CallManager getInstance() {
        Log.d(Log.TAG, "CallManager getInstance");
        return null;
    }
    public void registerForNewRingingConnection(Handler h, int what, Object obj){
        Log.d(Log.TAG, "registerForNewRingingConnection");
    }
    
    public void registerForPreciseCallStateChanged(Handler h, int what, Object obj){
        Log.d(Log.TAG, "registerForPreciseCallStateChanged");
    }
    
    public void registerForDisconnect(Handler h, int what, Object obj) {
        Log.d(Log.TAG, "registerForDisconnect");
    }
    
    public void registerForDisplayInfo(Handler h, int what, Object obj){
        Log.d(Log.TAG, "registerForDisplayInfo");
    }
    
    public void registerForSignalInfo(Handler h, int what, Object obj){
        Log.d(Log.TAG, "registerForSignalInfo");
    }
    
    public void registerForSuppServiceNotification(Handler h, int what, Object obj){
        Log.d(Log.TAG, "registerForSuppServiceNotification");
    }
    
    public void registerForIncomingRing(Handler h, int what, Object obj){
        Log.d(Log.TAG, "registerForIncomingRing");
    }
    
    public void registerForCallWaiting(Handler h, int what, Object obj){
        Log.d(Log.TAG, "registerForCallWaiting");
    }
    
    public void unregisterForNewRingingConnection(Handler h){
        Log.d(Log.TAG, "unregisterForNewRingingConnection");
    }
    
    public void unregisterForPreciseCallStateChanged(Handler h){
        Log.d(Log.TAG, "unregisterForPreciseCallStateChanged");
    }
    public void unregisterForDisconnect(Handler h){
        Log.d(Log.TAG, "unregisterForDisconnect");
    }
    
    public void unregisterForIncomingRing(Handler h){
        Log.d(Log.TAG, "unregisterForIncomingRing");
    }
    public void unregisterForCallWaiting(Handler h){
        Log.d(Log.TAG, "unregisterForCallWaiting");
    }
    public void unregisterForDisplayInfo(Handler h) {
        Log.d(Log.TAG, "unregisterForDisplayInfo");
    }
    
    public void unregisterForSignalInfo(Handler h){
        Log.d(Log.TAG, "unregisterForSignalInfo");
    }
    
    public void unregisterForSuppServiceNotification(Handler h){
        Log.d(Log.TAG, "unregisterForSuppServiceNotification");
    }
}
