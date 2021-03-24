package com.example.gymap;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gymap.data.GymContract;

public class GymCursorAdapter extends CursorAdapter {

    //Constructor
    public GymCursorAdapter(Context context, Cursor c){
        super(context, c, 0);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView)view.findViewById(R.id.name);
        TextView summaryTextView = (TextView)view.findViewById(R.id.summary);

        // Find the columns of members attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(GymContract.GymEntry.COLUMN_NAME);
        int weightColumnIndex = cursor.getColumnIndex(GymContract.GymEntry.COLUMN_WEIGHT);

        // Read the members attributes from the Cursor for the current pet
        String name = cursor.getString(nameColumnIndex);
        int weight = cursor.getInt(weightColumnIndex);

        // Update the TextViews with the attributes for the current member
        nameTextView.setText(name);
        summaryTextView.setText(Integer.toString(weight));
    }
}
