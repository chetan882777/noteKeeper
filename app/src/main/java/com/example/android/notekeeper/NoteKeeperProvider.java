package com.example.android.notekeeper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.android.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import static com.example.android.notekeeper.NoteKeeperProviderContract.*;

public class NoteKeeperProvider extends ContentProvider {

    private static final String MIME_VENDOR_TYPE = "vnd." + NoteKeeperProviderContract.AUTHORITY + ".";
    private NoteKeeperOpenHelper mOpenHelper;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES = 0;

    public static final int NOTES = 1;

    public static final int NOTES_EXPANDED = 2;

    public static final int NOTES_ROW = 3;

    static {
        sUriMatcher.addURI(AUTHORITY , Courses.PATH, COURSES);
        sUriMatcher.addURI(AUTHORITY , Notes.PATH, NOTES);
        sUriMatcher.addURI(AUTHORITY , Notes.PATH_EXPANDED , NOTES_EXPANDED);
        sUriMatcher.addURI(AUTHORITY , Notes.PATH  + "/#" , NOTES_ROW);
    }

    public NoteKeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {

        String mimeType = null;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case COURSES:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Courses.PATH;
                break;

            case NOTES:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH;
                break;

            case NOTES_EXPANDED:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH_EXPANDED;
                break;

            case NOTES_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Notes.PATH;
                break;
        }

        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = -1 ;
        Uri rowUri = null;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch){
            case COURSES:
                rowId = db.insert(CourseInfoEntry.TABLE_NAME , null , values);
                rowUri = ContentUris.withAppendedId(Courses.CONTENT_URI , rowId);
                break;

            case NOTES:
                rowId = db.insert(NoteInfoEntry.TABLE_NAME , null , values);
                rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI , rowId);
                break;

            case NOTES_EXPANDED:
                // throws exception saying that's is read only table
                break;
        }

        return rowUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor = null;

        int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch){
            case COURSES :
                cursor = db.query(CourseInfoEntry.TABLE_NAME , projection , selection , selectionArgs ,
                    null, null , sortOrder);
                break;

            case NOTES :
                cursor = db.query(NoteInfoEntry.TABLE_NAME , projection , selection , selectionArgs ,
                        null, null , sortOrder);
                break;

            case NOTES_EXPANDED:cursor =  notesExpandedQUery(db , projection , selection , selectionArgs , sortOrder);
                break;

            case NOTES_ROW:
                long rowId = ContentUris.parseId(uri);
                String rowSelection = NoteInfoEntry._ID + " = ?";
                String[] rowSlectionArgs = new String[]{Long.toString(rowId)};

                cursor = db.query(NoteInfoEntry.TABLE_NAME , projection , rowSelection ,
                        rowSlectionArgs , null , null , null);
                break;
        }
        return cursor;
    }

    private Cursor notesExpandedQUery(SQLiteDatabase db, String[] projection,
                                      String selection, String[] selectionArgs, String sortOrder) {
        String[] columns = new String[projection.length];

        for(int idx = 0 ; idx < projection.length ; idx++){
            columns[idx] = projection[idx].equals(BaseColumns._ID ) ||
                    projection[idx].equals(CourseIdColumn.COLUMN_COURSE_ID)?
                    NoteInfoEntry.getQName(projection[idx]): projection[idx];
        }

        String tablesWithJoin = NoteInfoEntry.TABLE_NAME
                + " JOIN " + CourseInfoEntry.TABLE_NAME
                + " ON " + NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID)
                + " = " + CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);

        return db.query(tablesWithJoin , columns , selection , selectionArgs ,
                null , null , sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
