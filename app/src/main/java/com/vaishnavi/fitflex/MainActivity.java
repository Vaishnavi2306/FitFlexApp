package com.vaishnavi.fitflex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText, genderEditText, dobEditText, startDateEditText, targetDateEditText;
    private EditText weightEditText, heightEditText, targetWeightEditText;
    private Button saveButton;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Bind views
        nameEditText = findViewById(R.id.editTextName);
        ageEditText = findViewById(R.id.editTextAge);
        genderEditText = findViewById(R.id.editTextGender);
        dobEditText = findViewById(R.id.editTextDob);
        startDateEditText = findViewById(R.id.editTextStartDate);
        targetDateEditText = findViewById(R.id.editTextTargetDate);
        weightEditText = findViewById(R.id.editTextWeight);
        heightEditText = findViewById(R.id.editTextHeight);
        targetWeightEditText = findViewById(R.id.editTextTargetWeight);
        saveButton = findViewById(R.id.buttonSave);

        // Set up save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    // Method to save user data
    private void saveUserData() {
        try {
            // Get user input
            String name = nameEditText.getText().toString();
            int age = Integer.parseInt(ageEditText.getText().toString());
            String gender = genderEditText.getText().toString();
            String dob = dobEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String targetDate = targetDateEditText.getText().toString();
            float weight = Float.parseFloat(weightEditText.getText().toString());
            float height = Float.parseFloat(heightEditText.getText().toString());
            float targetWeight = Float.parseFloat(targetWeightEditText.getText().toString());

            // Calculate BMI
            float bmi = databaseHelper.calculateBMI(weight, height);

            // Calculate total days between start and target dates
            long totalDays = calculateTotalDays(startDate, targetDate);

            // Save data in database
            databaseHelper.saveData(name, age, gender, dob, startDate, targetDate, weight, height, targetWeight, bmi, totalDays);

            Toast.makeText(MainActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

            // After saving, navigate to the login screen
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("MainActivity", "Error while saving data: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
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
