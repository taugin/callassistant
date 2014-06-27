package com.android.callassistant.info;

public class RecordInfo implements Comparable<RecordInfo> {
    public static int checkedNumber = 0;
    public int recordId;
    public boolean play;
    public String recordFile;
    public String recordName;
    public long recordSize;
    public long recordRing;
    public long recordStart;
    public long recordEnd;
    public int callFlag;
    public boolean checked;

    @Override
    public int compareTo(RecordInfo another) {
        return Long.valueOf(another.recordStart - recordStart).intValue();
    }
}
