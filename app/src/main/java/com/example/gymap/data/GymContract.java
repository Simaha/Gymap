package com.example.gymap.data;

import android.provider.BaseColumns;

public class GymContract {

    public static final class GymEntry implements BaseColumns{

        public static final String TABLE_NAME = "members";
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
