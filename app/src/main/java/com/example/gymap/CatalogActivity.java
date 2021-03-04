package com.example.gymap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gymap.data.GymContract;
import com.example.gymap.data.GymContract.GymEntry;
import com.example.gymap.data.GymDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity {

    private GymDbHelper mDbHelper = new GymDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo(){
        GymDbHelper mDbHelper = new GymDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Define projection to be used with the query method
        String[] projection = {
                BaseColumns._ID,
                GymEntry.COLUMN_NAME,
                GymEntry.COLUMN_AGE,
                GymEntry.COLUMN_GENDER,
                GymEntry.COLUMN_WEIGHT
        };

        Cursor cursor = db.query(
                GymContract.GymEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        TextView displayView = findViewById(R.id.text_view_gym);

        try {
            displayView.setText("Number of Rows in Gym database table: " + cursor.getCount() + "\n\n");
            displayView.append(GymEntry._ID + " - " +
                    GymEntry.COLUMN_NAME + "\n");

            //Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(GymEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(GymEntry.COLUMN_NAME);

            //Iterate through all the returned rows in  the curosr
            while (cursor.moveToNext()) {
                //Use that index to extract the String or Int value of the word
                // at the current word the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append("\n" + currentID + " - " + currentName);
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded member data into the database. For debugging purposes only.
     */
    private void insertMember(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GymEntry.COLUMN_NAME, "Ebrima Simaha");
        values.put(GymEntry.COLUMN_AGE, 27);
        values.put(GymEntry.COLUMN_GENDER, 1);
        values.put(GymEntry.COLUMN_WEIGHT, 60);

        long newRowId = db.insert(GymEntry.TABLE_NAME, null, values);

        Log.v("CatalogActivity", "New row ID " + newRowId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertMember();
                displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}