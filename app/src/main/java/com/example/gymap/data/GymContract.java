package com.example.gymap.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class GymContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private GymContract(){}

    //Initiate the content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.gymap";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_GYMAP = "gymap";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class GymEntry implements BaseColumns{

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GYMAP);

        /** Name of database for Gymap*/
        public static final String TABLE_NAME = "members";

        //Name of columns in Gymap database
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT = "weight";

        /*
         * Possible value for the gender of the members.
         */
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }
}
