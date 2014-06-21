package com.android.phonerecorder.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_BASEINFO_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBConstant.TABLE_BASEINFO
          + "("
          + DBConstant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + DBConstant.BASEINFO_NAME + " TEXT,"
          + DBConstant.BASEINFO_SEX + " INTEGER,"
          + DBConstant.BASEINFO_AGE + " INTEGER,"
          + DBConstant.BASEINFO_ADDRESS + " TEXT,"
          + DBConstant.BASEINFO_NUMBER + " TEXT,"
          + DBConstant.BASEINFO_CALL_LOG_COUNT + " INTEGER DEFAULT 0,"
          + DBConstant.BASEINFO_ALLOW_RECORD + " INTEGER DEFAULT 1,"
          + DBConstant.BASEINFO_STATE + " TEXT,"
          + DBConstant.BASEINFO_UPDATE + " LONG DEFAULT 0,"
          + DBConstant.FOO + " text"
          + ")";
    private final String DROP_BASEINFO_TABLE = "DROP TABLE " + DBConstant.TABLE_BASEINFO + " IF EXISTS";

    private static final String CREATE_RECORD_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DBConstant.TABLE_RECORD
          + "("
          + DBConstant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + DBConstant.RECORD_BASEINFO_ID + " INTEGER REFERENCES " + DBConstant.TABLE_BASEINFO + "(" + DBConstant._ID + "),"
          + DBConstant.RECORD_NAME + " TEXT,"
          + DBConstant.RECORD_FILE + " TEXT,"
          + DBConstant.RECORD_NUMBER + " TEXT,"
          + DBConstant.RECORD_FLAG + " INTEGER DEFAULT 0,"
          + DBConstant.RECORD_SIZE + " LONG DEFAULT 0,"
          + DBConstant.RECORD_START + " LONG DEFAULT 0,"
          + DBConstant.RECORD_END + " LONG DEFAULT 0,"
          + DBConstant.FOO + " text"
          + ")";
    private final String DROP_RECORD_TABLE = "DROP TABLE " + DBConstant.TABLE_RECORD + " IF EXISTS";

    private Context mContext;
    public DBHelper(Context context) {
        super(context, DBConstant.DB_NAME, null, DBConstant.DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("taugin", "CREATE_RECORD_FILE = " + CREATE_RECORD_TABLE);
        try{
            db.execSQL(CREATE_BASEINFO_TABLE);
            db.execSQL(CREATE_RECORD_TABLE);
        }catch(SQLException e){
            Log.d("taugin", "create table failed e = " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            try{
                db.execSQL(DROP_RECORD_TABLE);
                db.execSQL(DROP_BASEINFO_TABLE);
            } catch(SQLException e){
                e.printStackTrace();
            } finally{
                onCreate(db);
            }
        }
    }

}
