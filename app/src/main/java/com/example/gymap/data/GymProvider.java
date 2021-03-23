package com.example.gymap.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.gymap.R;
import com.example.gymap.data.GymContract.GymEntry;



public class GymProvider extends ContentProvider {

    private GymDbHelper mDbHelper;

    /** Tag for the log messages **/
    public static final String LOG_TAG = GymProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the members table */
    private static final int MEMBERS = 100;

    /** URI matcher code for the content URI for a single member in the members table */
    private static final int MEMBER_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(GymContract.CONTENT_AUTHORITY, GymContract.PATH_GYMAP, MEMBERS);
        sUriMatcher.addURI(GymContract.CONTENT_AUTHORITY, GymContract.PATH_GYMAP+"/#", MEMBER_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new GymDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                projection = new String[]{
                        BaseColumns._ID,
                        GymEntry.COLUMN_NAME,
                        GymEntry.COLUMN_AGE,
                        GymEntry.COLUMN_GENDER,
                        GymEntry.COLUMN_WEIGHT
                };

                cursor = database.query(GymContract.GymEntry.TABLE_NAME,
                                        projection,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case MEMBER_ID:
                selection = GymContract.GymEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(GymEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                return insertMember(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMember(Uri uri, ContentValues values) {
        //Check that the name is not null
        String name = values.getAsString(GymEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalArgumentException("Member requires a name");
        }

        Integer age = values.getAsInteger(GymEntry.COLUMN_AGE);
        if (age == null && age < 15){
            throw new IllegalArgumentException("Member needs to be atleast 16 years Old");
        }

        //Check that the gender is valid
        Integer gender = values.getAsInteger(GymEntry.COLUMN_GENDER);
        if (gender == null || !GymEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Member requires valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(GymEntry.COLUMN_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Member requires valid weight");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new member with the given values
        long id = database.insert(GymEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null
        if(id == -1 ){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId( uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
