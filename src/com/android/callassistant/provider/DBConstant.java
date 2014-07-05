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
    public static final String RECORD_RING = "record_ring";
    public static final String RECORD_START = "record_start";
    public static final String RECORD_END = "record_end";

    public static final int FLAG_NONE = 0;
    public static final int FLAG_INCOMING = 1;
    public static final int FLAG_MISSCALL = 2;
    public static final int FLAG_BLOCKCALL = 3;
    public static final int FLAG_OUTGOING = 4;

    public static final String RECORD_CONTENT_TYPE = "vnd.android.cursor.item/vnd.record.items";
    public static final String RECORD_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.record.item";

    public static final Uri RECORD_URI = Uri.parse("content://" + AUTHORITIES + "/" + TABLE_RECORD);


    public static final String TABLE_CONTACTS = "table_contacts";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CONTACT_SEX = "CONTACT_SEX";
    public static final String CONTACT_AGE = "CONTACT_AGE";
    public static final String CONTACT_ADDRESS = "CONTACT_ADDRESS";
    public static final String CONTACT_NUMBER = "CONTACT_NUMBER";
    public static final String CONTACT_CALL_LOG_COUNT = "CONTACT_CALL_LOG_COUNT";
    public static final String CONTACT_ALLOW_RECORD = "CONTACT_ALLOW_RECORD";
    public static final String CONTACT_STATE = "CONTACT_STATE";
    public static final String CONTACT_UPDATE = "CONTACT_UPDATE";
    public static final String CONTACT_FROM_SYSTEM = "contact_from_system";
    
    public static final int FROM_SYSTEM_FALSE = 0;
    public static final int FROM_SYSTEM_TRUE = 1;

    public static final int ALLOW_RECORD = 1;
    public static final int FORBID_RECORD = 0;

    public static final String BASEINFO_CONTENT_TYPE = "vnd.android.cursor.item/vnd.baseinfo.items";
    public static final String BASEINFO_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.baseinfo.item";

    public static final Uri BASEINFO_URI = Uri.parse("content://" + AUTHORITIES + "/" + TABLE_CONTACTS);
    
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
