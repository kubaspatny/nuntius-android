package com.kubaspatny.startupanimation.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by Kuba on 21/9/2014.
 */
public class NuntiusContentProvider extends ContentProvider {

    private NuntiusDbHelper nuntiusDbHelper;

    private static final int ALL_MESSAGES = 1;
    private static final int SINGLE_MESSAGE = 2;
    private static final int LATEST_MESSAGES = 3;

    private static final String AUTHORITY = "com.kubaspatny.nuntius.nuntiuscontentprovider";

    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + NuntiusDataContract.MessageEntry.TABLE_NAME);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, NuntiusDataContract.MessageEntry.TABLE_NAME, ALL_MESSAGES);
        uriMatcher.addURI(AUTHORITY, NuntiusDataContract.MessageEntry.TABLE_NAME + "/#", SINGLE_MESSAGE);

        //TODO: what uri to add for LATEST_MESSAGES?
    }

    @Override
    public boolean onCreate() {
        nuntiusDbHelper = new NuntiusDbHelper(getContext());
        return false;
    }

    /**
     * Should follow these rules:
     *      vnd.android.cursor.dir/vnd.com.example.provider.table1  --- dir if more than one row
     *      vnd.android.cursor.item/vnd.com.example.provider.table1 --- item if only one row
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {

        switch(uriMatcher.match(uri)){
            case ALL_MESSAGES:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + NuntiusDataContract.MessageEntry.TABLE_NAME;
            case SINGLE_MESSAGE:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + NuntiusDataContract.MessageEntry.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase database = nuntiusDbHelper.getWritableDatabase();

        long id;

        switch (uriMatcher.match(uri)){
            case ALL_MESSAGES:
                id = database.insert(NuntiusDataContract.MessageEntry.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(CONTENT_URI + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = nuntiusDbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(NuntiusDataContract.MessageEntry.TABLE_NAME);

        switch(uriMatcher.match(uri)){
            case ALL_MESSAGES:
                // no additional arguments needed
                break;
            case SINGLE_MESSAGE:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(NuntiusDataContract.MessageEntry._ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }
                                                                                        // group by, having
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = nuntiusDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case ALL_MESSAGES:
                // no additional arguments needed
                break;
            case SINGLE_MESSAGE:
                String id = uri.getPathSegments().get(1);
                selection = NuntiusDataContract.MessageEntry._ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" : "AND (" + selection + ")");
                break;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);

        }

        int deletedRows = database.delete(NuntiusDataContract.MessageEntry.TABLE_NAME,
                                            selection,
                                            selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase database = nuntiusDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case ALL_MESSAGES:
                // no additional arguments needed
                break;
            case SINGLE_MESSAGE:
                String id = uri.getPathSegments().get(1);
                selection = NuntiusDataContract.MessageEntry._ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" : "AND (" + selection + ")");
                break;
            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);

        }

        int updatedRows = database.update(NuntiusDataContract.MessageEntry.TABLE_NAME,
                                            contentValues, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

}






















