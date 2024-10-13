package com.vaishnavi.fitflex;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText, dobEditText, startDateEditText, targetDateEditText;
    private EditText weightEditText, heightEditText, targetWeightEditText, bmiEditText, totalDaysEditText;
    private Spinner genderSpinner;
    private Button saveButton;
    private Calendar calendar;
    private SharedPreferences sharedPreferences;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("FitFlexPrefs", Context.MODE_PRIVATE);

        // Check if user data is already saved (allow only one user setup)
        if (sharedPreferences.getBoolean("isSetupComplete", false)) {
            // Redirect to the login page if setup is already complete
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            return; // Exit onCreate
        }

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Bind views
        nameEditText = findViewById(R.id.editTextName);
        ageEditText = findViewById(R.id.editTextAge);
        dobEditText = findViewById(R.id.editTextDob);
        startDateEditText = findViewById(R.id.editTextStartDate);
        targetDateEditText = findViewById(R.id.editTextTargetDate);
        weightEditText = findViewById(R.id.editTextWeight);
        heightEditText = findViewById(R.id.editTextHeight);
        targetWeightEditText = findViewById(R.id.editTextTargetWeight);
        bmiEditText = findViewById(R.id.editTextBmi);
        totalDaysEditText = findViewById(R.id.editTextTotalDays);
        genderSpinner = findViewById(R.id.spinnerGender);
        saveButton = findViewById(R.id.buttonSave);

        // Set up gender spinner (dropdown)
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Set up Calendar for date fields
        calendar = Calendar.getInstance();
        setupDatePickers();

        // Set up save button click listener
        saveButton.setOnClickListener(v -> {
            calculateBMIandDays(); // Calculate BMI and Total Days automatically
            saveUserData(); // Save all data to the database
        });
    }

    // Method to set up the Date Pickers for DOB, Start Date, and Target Date
    private void setupDatePickers() {
        // Date of Birth Picker
        dobEditText.setOnClickListener(v -> showDatePicker(dobEditText));

        // Start Date Picker
        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));

        // Target Date Picker
        targetDateEditText.setOnClickListener(v -> showDatePicker(targetDateEditText));
    }

    // Method to show date picker and set selected date in the EditText
    private void showDatePicker(final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    editText.setText(sdf.format(calendar.getTime()));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Method to save user data
    private void saveUserData() {
        try {
            // Get user input
            String name = nameEditText.getText().toString();
            int age = Integer.parseInt(ageEditText.getText().toString());
            String gender = genderSpinner.getSelectedItem().toString();
            String dob = dobEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String targetDate = targetDateEditText.getText().toString();
            float weight = Float.parseFloat(weightEditText.getText().toString());
            float height = Float.parseFloat(heightEditText.getText().toString());
            float targetWeight = Float.parseFloat(targetWeightEditText.getText().toString());
            float bmi = Float.parseFloat(bmiEditText.getText().toString());
            long totalDays = Long.parseLong(totalDaysEditText.getText().toString());

            // Save data in the database
            databaseHelper.saveUserData(name, age, gender, dob, startDate, targetDate, weight, height, targetWeight, bmi, totalDays);

            // Set setup complete flag in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isSetupComplete", true);
            editor.apply();

            Toast.makeText(MainActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

            // After saving, navigate to the login page
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("MainActivity", "Error while saving data: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to calculate BMI and total days before saving
    private void calculateBMIandDays() {
        try {
            // Get weight and height for BMI calculation
            float weight = Float.parseFloat(weightEditText.getText().toString());
            float height = Float.parseFloat(heightEditText.getText().toString());
            float bmi = calculateBMI(weight, height);
            bmiEditText.setText(String.valueOf(bmi)); // Set BMI value

            // Get start and target dates for total days calculation
            String startDate = startDateEditText.getText().toString();
            String targetDate = targetDateEditText.getText().toString();
            long totalDays = calculateTotalDays(startDate, targetDate);
            totalDaysEditText.setText(String.valueOf(totalDays)); // Set total days value
        } catch (Exception e) {
            Log.e("MainActivity", "Error calculating BMI or total days: " + e.getMessage());
        }
    }

    // Method to calculate BMI
    private float calculateBMI(float weight, float height) {
        return weight / (height * height); // Assuming height is in meters
    }

    // Method to calculate total days between start date and target date
    private long calculateTotalDays(String startDate, String targetDate) {
        try {
            // Assuming dates are in the format "yyyy-MM-dd"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = dateFormat.parse(startDate);
            Date target = dateFormat.parse(targetDate);

            // Calculate difference in milliseconds and convert to days
            long diffInMillis = target.getTime() - start.getTime();
            return diffInMillis / (1000 * 60 * 60 * 24); // Convert milliseconds to days
        } catch (Exception e) {
            Log.e("MainActivity", "Error calculating total days: " + e.getMessage());
            return 0; // Return 0 if there's an error
        }
    }
}
