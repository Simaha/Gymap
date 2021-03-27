package com.example.gymap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gymap.data.GymContract;
import com.example.gymap.data.GymContract.GymEntry;
import com.example.gymap.data.GymDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int GYM_LOADER = 0;

    GymCursorAdapter mCursorAdapter;

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

        ListView gymListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        gymListView.setEmptyView(emptyView);

        mCursorAdapter = new GymCursorAdapter(this, null);
        gymListView.setAdapter(mCursorAdapter);

        gymListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentMemberUri = ContentUris.withAppendedId(GymEntry.CONTENT_URI, id);

                intent.setData(currentMemberUri);

                startActivity(intent);
            }

        });

        //Kick off the loader
        getSupportLoaderManager().initLoader(GYM_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded member data into the database. For debugging purposes only.
     */
    private void insertMember(){
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(GymEntry.COLUMN_NAME, "Alhassan Simaha");
        values.put(GymEntry.COLUMN_AGE,32);
        values.put(GymEntry.COLUMN_GENDER, GymEntry.GENDER_MALE);
        values.put(GymEntry.COLUMN_WEIGHT, 30);

        Uri newUri = getContentResolver().insert(GymEntry.CONTENT_URI, values);

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
                return true;
            case R.id.action_delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that speifies the columns from the table we care about.
        String[] projection = {
            GymEntry._ID,
            GymEntry.COLUMN_NAME,
            GymEntry.COLUMN_WEIGHT };

        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                GymEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}