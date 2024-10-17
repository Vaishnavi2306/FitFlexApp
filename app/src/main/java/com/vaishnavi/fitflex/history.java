package com.vaishnavi.fitflex;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class history extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView nameTextView, genderTextView, ageTextView;
    private RecyclerView recyclerView;
    private ProgressAdapter progressAdapter;
    private Button deleteButton;
    private List<ProgressData> progressList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        nameTextView = findViewById(R.id.nameTextView);
        genderTextView = findViewById(R.id.genderTextView);
        ageTextView = findViewById(R.id.ageTextView);
        recyclerView = findViewById(R.id.recyclerViewProgress);
        deleteButton = findViewById(R.id.deleteButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Load user data and progress data
        loadUserData();
        loadProgressData();

        // Set up delete button click listener
        deleteButton.setOnClickListener(v -> confirmDeleteSelectedItems());
    }

    private void loadUserData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_DETAILS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));
            int age = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE));

            nameTextView.setText(name);
            genderTextView.setText(gender);
            ageTextView.setText(String.valueOf(age));

        } else {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void loadProgressData() {
        progressList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query the daily_progress table and order by date in ascending order
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME_DAILY_PROGRESS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_PROGRESS_DATE + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESS_DATE));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESS_WEIGHT));
                float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESS_BMI));
                byte[] imageBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESS_IMAGE));

                // Convert image byte array to Bitmap if available
                Bitmap bitmap = null;
                if (imageBlob != null) {
                    bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
                }

                // Add the progress data to the list
                progressList.add(new ProgressData(date, weight, bmi, bitmap));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No progress data found", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }

        // Setup RecyclerView with the adapter
        progressAdapter = new ProgressAdapter(progressList);
        recyclerView.setAdapter(progressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void confirmDeleteSelectedItems() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Selected Items")
                .setMessage("Are you sure you want to delete the selected items?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteSelectedItemsFromDatabase();
                    progressAdapter.deleteSelectedItems(); // Delete from RecyclerView
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteSelectedItemsFromDatabase() {
        List<ProgressData> selectedItems = progressAdapter.getSelectedItems();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (ProgressData item : selectedItems) {
            db.delete(DatabaseHelper.TABLE_NAME_DAILY_PROGRESS,
                    DatabaseHelper.COLUMN_PROGRESS_DATE + " = ?",
                    new String[]{item.getDate()});
        }

        Toast.makeText(this, "Selected items deleted", Toast.LENGTH_SHORT).show();
    }
}
