package com.android.callassistant.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.callassistant.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_CONTACT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBConstant.TABLE_CONTACTS
          + "("
          + DBConstant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + DBConstant.CONTACT_NAME + " TEXT,"
          + DBConstant.CONTACT_SEX + " INTEGER,"
          + DBConstant.CONTACT_AGE + " INTEGER,"
          + DBConstant.CONTACT_ADDRESS + " TEXT,"
          + DBConstant.CONTACT_NUMBER + " TEXT,"
          + DBConstant.CONTACT_CALL_LOG_COUNT + " INTEGER DEFAULT 0,"
          + DBConstant.CONTACT_ALLOW_RECORD + " INTEGER DEFAULT 1,"
          + DBConstant.CONTACT_STATE + " TEXT,"
          + DBConstant.CONTACT_UPDATE + " LONG DEFAULT 0,"
          + DBConstant.CONTACT_MODIFY_NAME + " INTEGER DEFAULT 0,"
          + DBConstant.FOO + " text"
          + ")";
    private static final String DROP_CONTACT_TABLE = "DROP TABLE " + DBConstant.TABLE_CONTACTS + " IF EXISTS";

    private static final String CREATE_RECORD_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBConstant.TABLE_RECORD
          + "("
          + DBConstant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + DBConstant.RECORD_CONTACT_ID + " INTEGER REFERENCES " + DBConstant.TABLE_CONTACTS + "(" + DBConstant._ID + "),"
          + DBConstant.RECORD_NAME + " TEXT,"
          + DBConstant.RECORD_FILE + " TEXT,"
          + DBConstant.RECORD_NUMBER + " TEXT,"
          + DBConstant.RECORD_FLAG + " INTEGER DEFAULT 0,"
          + DBConstant.RECORD_SIZE + " LONG DEFAULT 0,"
          + DBConstant.RECORD_RING + " LONG DEFAULT 0,"
          + DBConstant.RECORD_START + " LONG DEFAULT 0,"
          + DBConstant.RECORD_END + " LONG DEFAULT 0,"
          + DBConstant.FOO + " text"
          + ")";
    private static final String DROP_RECORD_TABLE = "DROP TABLE " + DBConstant.TABLE_RECORD + " IF EXISTS";

    private static final String CREATE_BLOCK_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBConstant.TABLE_BLOCK
          + "("
          + DBConstant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + DBConstant.BLOCK_NAME + " TEXT,"
          + DBConstant.BLOCK_NUMBER + " TEXT UNIQUE,"
          + DBConstant.BLOCK_COUNT + " INTEGER DEFAULT 0,"
          + DBConstant.BLOCK_TIME + " LONG DEFAULT 0,"
          + DBConstant.BLOCK_HIS_TIMES + " TEXT,"
          + DBConstant.BLOCK_TYPE + " INTEGER DEFAULT 0,"
          + DBConstant.BLOCK_CONTENT + " TEXT"
          + ")";
    private static final String DROP_BLOCK_TABLE = "DROP TABLE " + DBConstant.TABLE_RECORD + " IF EXISTS";

    private static final String TRIGGER_ON_DELETE_RECORD =
            "CREATE TRIGGER DELETE_RECORD_TRIGGER AFTER DELETE ON " + DBConstant.TABLE_RECORD
          + " FOR EACH ROW "
          + " BEGIN "
          + " UPDATE " + DBConstant.TABLE_CONTACTS + " SET " + DBConstant.CONTACT_CALL_LOG_COUNT + "=" + DBConstant.CONTACT_CALL_LOG_COUNT + "-1 "
          + " WHERE " + DBConstant.TABLE_CONTACTS + "." + DBConstant._ID + "=" + "OLD." + DBConstant.RECORD_CONTACT_ID + ";"
          + " END;";
    private static final String DROP_TRIGGER_RECORD = "DROP TRIGGER DELETE_RECORD_TRIGGER";
    private Context mContext;

    public DBHelper(Context context) {
        super(context, DBConstant.DB_NAME, null, DBConstant.DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(Log.TAG, "CREATE_RECORD_FILE = " + CREATE_RECORD_TABLE);
        try{
            db.execSQL(CREATE_CONTACT_TABLE);
            db.execSQL(CREATE_RECORD_TABLE);
            db.execSQL(CREATE_BLOCK_TABLE);
            db.execSQL(TRIGGER_ON_DELETE_RECORD);
        }catch(SQLException e){
            Log.d(Log.TAG, "create table failed e = " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            try{
                db.execSQL(DROP_RECORD_TABLE);
                db.execSQL(DROP_CONTACT_TABLE);
                db.execSQL(DROP_BLOCK_TABLE);
                db.execSQL(DROP_TRIGGER_RECORD);
            } catch(SQLException e){
                e.printStackTrace();
            } finally{
                onCreate(db);
            }
        }
    }

}
