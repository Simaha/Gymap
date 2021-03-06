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
import android.text.Selection;
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
        //Set notification URI on the cursor
        //so we know what content URI the content was created for
        //If the data at this URI changes, then we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

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

        //Notif all listeners that eh data  has changed for the member content URI
        getContext().getContentResolver().notifyChange(uri, null);

        //return the new URI with the ID of the newly inserted roa appended at the time
        return ContentUris.withAppendedId( uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case MEMBERS:
                return updateMembers(uri, contentValues, selection, selectionArgs);
            case MEMBER_ID:
                selection = GymEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMembers(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMembers(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(GymEntry.COLUMN_NAME)){
            String name = values.getAsString(GymEntry.COLUMN_NAME);
            if(name == null){
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(GymEntry.COLUMN_AGE)){
            Integer age = values.getAsInteger(GymEntry.COLUMN_AGE);
            if (age == null && age < 15) {
                throw new IllegalArgumentException("Member needs to be atleast 16 years Old");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(GymEntry.COLUMN_GENDER)){
            Integer gender = values.getAsInteger(GymEntry.COLUMN_GENDER);
            if (gender == null || !GymEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Member requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(GymEntry.COLUMN_WEIGHT)){
            Integer weight = values.getAsInteger(GymEntry.COLUMN_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Member requires valid weight");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(GymEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get writeable database
        SQLiteDatabase database =mDbHelper.getWritableDatabase();

        //Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                rowsDeleted = database.delete(GymEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = GymEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(GymEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);


        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                return GymEntry.CONTENT_LIST_TYPE;
            case MEMBER_ID:
                return GymEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
