package com.vaishnavi.fitflex;

import static com.vaishnavi.fitflex.DatabaseHelper.TABLE_NAME_USER_DETAILS;
import static com.vaishnavi.fitflex.DatabaseHelper.TABLE_NAME_DAILY_PROGRESS;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText nameEditText, ageEditText, weightEditText, heightEditText, targetWeightEditText, dobEditText, startDateEditText, targetDateEditText, bmiEditText, totalDaysEditText;
    private Spinner genderSpinner;
    private Button updateButton, deleteButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);  // Use your actual layout file

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Bind views
        nameEditText = findViewById(R.id.editTextName);
        ageEditText = findViewById(R.id.editTextAge);
        weightEditText = findViewById(R.id.editTextWeight);
        heightEditText = findViewById(R.id.editTextHeight);
        targetWeightEditText = findViewById(R.id.editTextTargetWeight);
        dobEditText = findViewById(R.id.editTextDob);
        startDateEditText = findViewById(R.id.editTextStartDate);
        targetDateEditText = findViewById(R.id.editTextTargetDate);
        bmiEditText = findViewById(R.id.editTextBmi);  // Add this to display BMI
        totalDaysEditText = findViewById(R.id.editTextTotalDays);  // Add this to display total days
        genderSpinner = findViewById(R.id.spinnerGender);
        updateButton = findViewById(R.id.buttonUpdate);  // Add update button for saving changes
        deleteButton = findViewById(R.id.buttonDelete);  // Add delete button

        // Initialize calendar for date pickers
        calendar = Calendar.getInstance();

        // Set up DatePickerDialog for dob, startDate, and targetDate
        setupDatePickers();

        // Display user details from the database
        displayUserDetails();

        // Set up update button to save changes
        updateButton.setOnClickListener(v -> updateUserDetails());

        // Set up delete button to remove data from the database
        deleteButton.setOnClickListener(v -> confirmDeletion());
    }

    // Method to display user details from the database
    private void displayUserDetails() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USER_DETAILS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Fetch data from the database and display in the form
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                int age = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));
                float height = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT));
                float targetWeight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_WEIGHT));
                String dob = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOB));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_DATE));
                String targetDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_DATE));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));

                // Set data to the respective EditText fields
                nameEditText.setText(name);
                ageEditText.setText(String.valueOf(age));
                weightEditText.setText(String.valueOf(weight));
                heightEditText.setText(String.valueOf(height));
                targetWeightEditText.setText(String.valueOf(targetWeight));
                dobEditText.setText(dob);
                startDateEditText.setText(startDate);
                targetDateEditText.setText(targetDate);

                // Calculate and display BMI and total days
                calculateAndDisplayBMI(weight, height);
                calculateAndDisplayTotalDays(startDate, targetDate);

                // Set the gender spinner selection based on the gender value
                if (gender != null) {
                    if (gender.equalsIgnoreCase("Male")) {
                        genderSpinner.setSelection(0);
                    } else if (gender.equalsIgnoreCase("Female")) {
                        genderSpinner.setSelection(1);
                    } else {
                        genderSpinner.setSelection(2);  // Default for other
                    }
                }

            } catch (Exception e) {
                Log.e("AccountActivity", "Error fetching user details: " + e.getMessage());
            } finally {
                cursor.close();  // Close cursor after use
            }
        } else {
            Toast.makeText(this, "No user details found", Toast.LENGTH_SHORT).show();
            Log.d("AccountActivity", "No data found in the user_details table.");
        }
    }

    // Method to calculate and display BMI
    private void calculateAndDisplayBMI(float weight, float height) {
        if (height > 0) {
            float bmi = weight / (height * height);  // Assuming height is in meters
            bmiEditText.setText(String.valueOf(bmi));
        }
    }

    // Method to calculate and display total days between startDate and targetDate
    private void calculateAndDisplayTotalDays(String startDate, String targetDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            long start = sdf.parse(startDate).getTime();
            long target = sdf.parse(targetDate).getTime();
            long totalDays = (target - start) / (1000 * 60 * 60 * 24);  // Convert milliseconds to days
            totalDaysEditText.setText(String.valueOf(totalDays));
        } catch (Exception e) {
            Log.e("AccountActivity", "Error calculating total days: " + e.getMessage());
        }
    }

    // Method to update user details
    private void updateUserDetails() {
        try {
            String name = nameEditText.getText().toString();
            int age = Integer.parseInt(ageEditText.getText().toString());
            float weight = Float.parseFloat(weightEditText.getText().toString());
            float height = Float.parseFloat(heightEditText.getText().toString());
            float targetWeight = Float.parseFloat(targetWeightEditText.getText().toString());
            String dob = dobEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String targetDate = targetDateEditText.getText().toString();
            String gender = genderSpinner.getSelectedItem().toString();

            // Recalculate BMI and total days in case the dates or weight/height have been updated
            calculateAndDisplayBMI(weight, height);
            calculateAndDisplayTotalDays(startDate, targetDate);

            // Update the user details in the database
            databaseHelper.updateUserData(name, age, gender, dob, startDate, targetDate, weight, height, targetWeight, Float.parseFloat(bmiEditText.getText().toString()), Long.parseLong(totalDaysEditText.getText().toString()));

            Toast.makeText(this, "Details updated successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("AccountActivity", "Error updating user details: " + e.getMessage());
            Toast.makeText(this, "Error updating details", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to show a confirmation dialog and delete user data
    private void confirmDeletion() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action deletes the entire data.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteUserData())  // If "Yes" is clicked
                .setNegativeButton(android.R.string.no, null)  // If "No" is clicked
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    // Method to set up DatePickerDialogs for dob, startDate, and targetDate
    private void setupDatePickers() {
        dobEditText.setOnClickListener(v -> showDatePickerDialog(dobEditText));
        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText));
        targetDateEditText.setOnClickListener(v -> showDatePickerDialog(targetDateEditText));
    }

    // Method to show a DatePickerDialog
    private void showDatePickerDialog(EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String dateFormat = "yyyy-MM-dd"; // Adjust to your desired format
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            editText.setText(sdf.format(calendar.getTime()));
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    // Method to delete user data from both user_details and daily_progress tables
    private void deleteUserData() {
        databaseHelper.deleteUserData();  // Delete from user_details
        databaseHelper.deleteAllDailyProgress();  // Delete from daily_progress

        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

        // Optionally, redirect to a welcome or main page after deletion
        finish();  // Close the account activity
    }
}
