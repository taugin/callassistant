package com.android.phonerecorder.provider;

import android.net.Uri;

public class DBConstant {

    public static final String AUTHORITIES = "com.android.phonerecorder";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "phonerecorder.db";
    public static final String _ID = "_id";
    public static final String FOO = "foo";

    public static final String TABLE_RECORD = "record_table";
    public static final String RECORD_NAME = "record_name";
    public static final String RECORD_FILE = "record_file";
    public static final String RECORD_NUMBER = "record_number";
    public static final String RECORD_FLAG = "record_flag";
    public static final String RECORD_SIZE = "record_size";
    public static final String RECORD_START = "record_start";
    public static final String RECORD_END = "record_end";

    public static final int FLAG_INCOMING = 0;
    public static final int FLAG_OUTGOING = 1;

    public static final String RECORD_CONTENT_TYPE = "vnd.android.cursor.item/vnd.record.items";
    public static final String RECORD_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.record.item";

    public static final Uri RECORD_URI = Uri.parse("content://" + AUTHORITIES + "/" + TABLE_RECORD);
}
