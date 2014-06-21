package com.android.phonerecorder.provider;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class RecordProvider extends ContentProvider {

    public final String TAG = "RecordProvider";

    private DBHelper mDBHelper = null;

    private static final int TABLE_RECORD = 0;
    private static final int TABLE_RECORD_ID = 1;
    private static final int TABLE_BASEINFO = 2;
    private static final int TABLE_BASEINFO_ID = 3;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(DBConstant.AUTHORITIES, DBConstant.TABLE_RECORD, TABLE_RECORD);
        sUriMatcher.addURI(DBConstant.AUTHORITIES, DBConstant.TABLE_RECORD + "/#", TABLE_RECORD_ID);

        sUriMatcher.addURI(DBConstant.AUTHORITIES, DBConstant.TABLE_BASEINFO, TABLE_BASEINFO);
        sUriMatcher.addURI(DBConstant.AUTHORITIES, DBConstant.TABLE_BASEINFO + "/#", TABLE_BASEINFO_ID);
    }
    @Override
    public boolean onCreate() {
        Log.d(TAG, "RecordProvider onCreate");
        mDBHelper = new DBHelper(getContext());
        if (mDBHelper != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType uri = " + uri);
        switch(sUriMatcher.match(uri)) {
        case TABLE_RECORD:
            return DBConstant.RECORD_CONTENT_TYPE;
        case TABLE_RECORD_ID:
            return DBConstant.RECORD_CONTENT_ITEM_TYPE;
        case TABLE_BASEINFO:
            return DBConstant.BASEINFO_CONTENT_TYPE;
        case TABLE_BASEINFO_ID:
            return DBConstant.BASEINFO_CONTENT_ITEM_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor c = null;
        long id = -1;
        try {
            switch(sUriMatcher.match(uri)){
            case TABLE_RECORD:
                c = db.query(DBConstant.TABLE_RECORD, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TABLE_RECORD_ID:
                id = ContentUris.parseId(uri);
                c = db.query(DBConstant.TABLE_RECORD, projection, DBConstant._ID + "=" + id, selectionArgs, null, null, sortOrder);
                break;
            case TABLE_BASEINFO:
                c = db.query(DBConstant.TABLE_BASEINFO, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TABLE_BASEINFO_ID:
                id = ContentUris.parseId(uri);
                c = db.query(DBConstant.TABLE_BASEINFO, projection, DBConstant._ID + "=" + id, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLException e){
            Log.d(TAG, e.getMessage());
            return null;
        }
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Log.d("taugin", "insert uri = " + uri);
        long id = -1;
        try{
            switch(sUriMatcher.match(uri)){
            case TABLE_RECORD:
                id = db.insert(DBConstant.TABLE_RECORD, DBConstant.FOO, values);
            break;
            case TABLE_BASEINFO:
                id = db.insert(DBConstant.TABLE_BASEINFO, DBConstant.FOO, values);
            break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }catch(SQLException e){
            Log.e("taugin", "The item has inserted into the database ! : " + e.getLocalizedMessage());
            Uri resultUri = ContentUris.withAppendedId(uri, 0);
            return resultUri;
        }
        Uri resultUri = ContentUris.withAppendedId(uri, id);
        Log.d("taugin", "resultUri = " + resultUri);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int ret = -1;
        long id = -1;
        try{
            switch(sUriMatcher.match(uri)){
            case TABLE_RECORD:
                ret = db.delete(DBConstant.TABLE_RECORD, selection, selectionArgs);
                break;
            case TABLE_RECORD_ID:
                id = ContentUris.parseId(uri);
                ret = db.delete(DBConstant.TABLE_RECORD, DBConstant._ID + "=" + id, selectionArgs);
                break;
            case TABLE_BASEINFO:
                ret = db.delete(DBConstant.TABLE_BASEINFO, selection, selectionArgs);
                break;
            case TABLE_BASEINFO_ID:
                id = ContentUris.parseId(uri);
                ret = db.delete(DBConstant.TABLE_BASEINFO, DBConstant._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }catch(SQLException e){
            Log.d(TAG, e.getMessage());
            return 0;
        }
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int ret = -1;
        long id = -1;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        try{
            switch(sUriMatcher.match(uri)){
            case TABLE_RECORD:
                ret = db.update(DBConstant.TABLE_RECORD, values, selection, selectionArgs);
                break;
            case TABLE_RECORD_ID:
                id = ContentUris.parseId(uri);
                ret = db.update(DBConstant.TABLE_RECORD, values, DBConstant._ID + "=" + id, selectionArgs);
                break;
            case TABLE_BASEINFO:
                ret = db.update(DBConstant.TABLE_BASEINFO, values, selection, selectionArgs);
                break;
            case TABLE_BASEINFO_ID:
                id = ContentUris.parseId(uri);
                ret = db.update(DBConstant.TABLE_BASEINFO, values, DBConstant._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } catch (SQLException e){
            Log.d(TAG, e.getMessage());
            return -1;
        }
        notifyChange(uri);
        return ret;
    }

    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.endTransaction();
        }
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
