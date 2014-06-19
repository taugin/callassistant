package com.android.phonerecorder;

public class RecordInfo implements Comparable<RecordInfo> {
    public boolean play;
    public String fileName;
    public String displayName;
    public long fileSize;
    public long fileCreateTime;
    public long fileLastTime;
    public boolean incoming;
    public boolean checked;

    @Override
    public int compareTo(RecordInfo another) {
        return Long.valueOf(another.fileCreateTime - fileCreateTime).intValue();
    }
}
