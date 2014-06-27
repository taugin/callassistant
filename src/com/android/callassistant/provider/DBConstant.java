package com.android.callassistant.provider;

import android.net.Uri;

public class DBConstant {

    public static final String AUTHORITIES = "com.android.callassistant";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "callassistant.db";
    public static final String _ID = "_id";
    public static final String FOO = "foo";

    public static final String TABLE_RECORD = "record_table";
    public static final String RECORD_BASEINFO_ID = "record_baseinfo_id";
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


    public static final String TABLE_BASEINFO = "baseinfo_table";
    public static final String BASEINFO_NAME = "baseinfo_name";
    public static final String BASEINFO_SEX = "baseinfo_sex";
    public static final String BASEINFO_AGE = "baseinfo_age";
    public static final String BASEINFO_ADDRESS = "baseinfo_address";
    public static final String BASEINFO_NUMBER = "baseinfo_number";
    public static final String BASEINFO_CALL_LOG_COUNT = "baseinfo_call_log_count";
    public static final String BASEINFO_ALLOW_RECORD = "baseinfo_allow_record";
    public static final String BASEINFO_STATE = "baseinfo_state";
    public static final String BASEINFO_UPDATE = "baseinfo_update";

    public static final int ALLOW_RECORD = 1;
    public static final int FORBID_RECORD = 0;

    public static final String BASEINFO_CONTENT_TYPE = "vnd.android.cursor.item/vnd.baseinfo.items";
    public static final String BASEINFO_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.baseinfo.item";

    public static final Uri BASEINFO_URI = Uri.parse("content://" + AUTHORITIES + "/" + TABLE_BASEINFO);
    
    public static final String TABLE_BLOCK = "block_table";
    public static final String BLOCK_NUMBER = "block_number";
    public static final String BLOCK_COUNT = "block_count";
    public static final String BLOCK_TIME = "block_time";
    public static final String BLOCK_TYPE = "block_type";
    public static final String BLOCK_CONTENT = "block_content";
    public static final int BLOCK_TYPE_CALL = 0;
    public static final int BLOCK_TYPE_MMS = 1;

    public static final String BLOCK_CONTENT_TYPE = "vnd.android.cursor.item/vnd.block.items";
    public static final String BLOCK_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.block.item";

    public static final Uri BLOCK_URI = Uri.parse("content://" + AUTHORITIES + "/" + TABLE_BLOCK);
}
