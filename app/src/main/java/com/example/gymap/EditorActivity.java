package com.example.gymap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.gymap.data.GymContract;
import com.example.gymap.data.GymContract.GymEntry;
import com.example.gymap.data.GymDbHelper;

import org.w3c.dom.Text;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MEMBER_LOADER = 0;

    private Uri mCurrentMemberUri;

    private int mGender = GymEntry.GENDER_MALE;

    private EditText mNameEditText;
    private EditText mAgeEditText;
    private Spinner mGenderSpinner;
    private EditText mWeightEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentMemberUri = intent.getData();

        //If the intent does not contain a member content uri, then we know that we are
        ///creating a neew member.
        if (mCurrentMemberUri == null) {
            //This is a new member, so change the app to say "Add a Member"
            setTitle("Add a Member");
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_member));
        }

            mNameEditText = findViewById(R.id.edit_name);
        mAgeEditText = findViewById(R.id.edit_age);
        mGenderSpinner = findViewById(R.id.spinner_gender);
        mWeightEditText = findViewById(R.id.edit_weight);

        setupSpinner();

        getSupportLoaderManager().initLoader(MEMBER_LOADER, null, this);
    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        //Set the integer mSelected to te constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if (selection.equals(getString(R.string.gender_male))){
                        mGender = GymEntry.GENDER_MALE;
                    }else {
                        mGender = GymEntry.GENDER_FEMALE;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mGender = GymEntry.GENDER_MALE;
            }
        });
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void insertMember(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String ageString = mAgeEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        int age = Integer.parseInt(ageString);
        int weight = Integer.parseInt(weightString);

        // Create a ContentValues object where column names are the keys,
        // and member attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(GymEntry.COLUMN_NAME, nameString);
        values.put(GymEntry.COLUMN_AGE, age);
        values.put(GymEntry.COLUMN_GENDER, mGender);
        values.put(GymEntry.COLUMN_WEIGHT, weight);

        // Insert a new row for pet in the database, returning the ID of that new row.
        Uri newUri = getContentResolver().insert(GymEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null){
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_member_failed), Toast.LENGTH_SHORT).show();
        }else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, getString(R.string.editor_insert_member_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            case R.id.action_save:
                //Save a new member to database
                insertMember();
                //Exit Activity
                finish();
                return true;
            case R.id.action_delete:
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Since the editor shows all members attributes, define a projection that contains
        // all columns from the gym table
        String[] projection = {
                GymEntry._ID,
                GymEntry.COLUMN_NAME,
                GymEntry.COLUMN_AGE,
                GymEntry.COLUMN_GENDER,
                GymEntry.COLUMN_WEIGHT
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentMemberUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(GymEntry.COLUMN_NAME);
            int ageColumnIndex = cursor.getColumnIndex(GymEntry.COLUMN_AGE);
            int genderColumnIndex = cursor.getColumnIndex(GymEntry.COLUMN_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(GymEntry.COLUMN_WEIGHT);

            //Extract the values from the cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int age = cursor.getInt(ageColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);

            //Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAgeEditText.setText(Integer.toString(age));
            mWeightEditText.setText(Integer.toString(weight));

            // Gender is a dropdown spinner, so map the constant value from the database
            switch(gender) {
                case GymEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                    case GymEntry.GENDER_FEMALE:
                        mGenderSpinner.setSelection(2);
                        break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
