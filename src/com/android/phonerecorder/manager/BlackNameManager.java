package com.android.phonerecorder.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BlackNameManager {

    // ռ��ʱת�ƣ���ʾ�����ĺ���Ϊ�պ�
    private final String ENABLE_SERVICE = "tel:**67*13800000000%23";
    // ռ��ʱת�ƣ���ʾ�����ĺ���Ϊ�ػ�
    private final String ENABLE_POWEROFF_SERVICE = "tel:**67*13810538911%23";
    // ռ��ʱת�ƣ���ʾ�����ĺ���Ϊͣ��
    private final String ENABLE_STOP_SERVICE = "tel:**21*13701110216%23";
    // ռ��ʱת��
    private final String DISABLE_SERVICE = "tel:%23%2321%23";

    private static BlackNameManager sBlackNameManager = null;
    private Context mContext;
    public static BlackNameManager getInstance(Context context) {
        if (sBlackNameManager == null) {
            sBlackNameManager = new BlackNameManager(context);
        }
        return sBlackNameManager;
    }

    private BlackNameManager(Context context) {
        mContext = context;
    }

    public boolean isBlack(String phoneName) {
        return false;
    }

    public void endCallForEmptyNumber() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(ENABLE_SERVICE));
        mContext.startActivity(i);
    }
    
    public void cancel(){
        Intent i = new Intent(Intent.ACTION_CALL);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(ENABLE_SERVICE));
        mContext.startActivity(i);
    }
}
