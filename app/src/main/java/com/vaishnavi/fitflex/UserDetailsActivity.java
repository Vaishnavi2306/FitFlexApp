package com.vaishnavi.fitflex;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserDetailsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView nameTextView, ageTextView, genderTextView, weightTextView, heightTextView, targetWeightTextView, bmiTextView, dobTextView, startDateTextView, targetDateTextView, totalDaysTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_deatils_activity);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // When the back button is clicked, navigate back to optionsActivity
            Intent intent = new Intent(UserDetailsActivity.this, optionsActivity.class);
            startActivity(intent);
            finish(); // Optionally, call finish() to close UserDetailsActivity
        });

        // Initialize views
        nameTextView = findViewById(R.id.nameTextView);
        ageTextView = findViewById(R.id.ageTextView);
        genderTextView = findViewById(R.id.genderTextView);
        weightTextView = findViewById(R.id.weightTextView);
        heightTextView = findViewById(R.id.heightTextView);
        targetWeightTextView = findViewById(R.id.targetWeightTextView);
        bmiTextView = findViewById(R.id.bmiTextView);
        dobTextView = findViewById(R.id.dobTextView);
        startDateTextView = findViewById(R.id.startDateTextView);
        targetDateTextView = findViewById(R.id.targetDateTextView);
        totalDaysTextView = findViewById(R.id.totalDaysTextView);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Display user details
        displayUserDetails();
    }

    // Method to display user details from the database
    private void displayUserDetails() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_DETAILS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Fetch data from the database and display in the TextViews
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                int age = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));
                float height = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT));
                float targetWeight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_WEIGHT));
                float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BMI));
                String dob = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOB));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_DATE));
                String targetDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_DATE));
                int totalDays = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL_DAYS));

                // Set data to the respective TextViews
                nameTextView.setText(name);
                ageTextView.setText(String.valueOf(age));
                genderTextView.setText(gender);
                weightTextView.setText(String.valueOf(weight));
                heightTextView.setText(String.valueOf(height));
                targetWeightTextView.setText(String.valueOf(targetWeight));
                bmiTextView.setText(String.valueOf(bmi));
                dobTextView.setText(dob);
                startDateTextView.setText(startDate);
                targetDateTextView.setText(targetDate);
                totalDaysTextView.setText(String.valueOf(totalDays));

            } catch (Exception e) {
                Log.e("UserDetailsActivity", "Error fetching user details: " + e.getMessage());
            } finally {
                cursor.close();  // Close the cursor after use
            }
        }
    }
}
