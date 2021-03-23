package com.example.gymap;

import android.content.ContentValues;
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

import com.example.gymap.data.GymContract;
import com.example.gymap.data.GymContract.GymEntry;
import com.example.gymap.data.GymDbHelper;

import org.w3c.dom.Text;

public class EditorActivity extends AppCompatActivity {
    private EditText mNameEditText;
    private EditText mAgeEditText;
    private Spinner mGenderSpinner;
    private EditText mWeightEditText;


    private int mGender = GymEntry.GENDER_MALE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = findViewById(R.id.edit_name);
        mAgeEditText = findViewById(R.id.edit_age);
        mGenderSpinner = findViewById(R.id.spinner_gender);
        mWeightEditText = findViewById(R.id.edit_weight);

        setupSpinner();
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
}
