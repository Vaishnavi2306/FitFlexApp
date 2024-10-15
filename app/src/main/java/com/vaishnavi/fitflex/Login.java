package com.vaishnavi.fitflex;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Login extends AppCompatActivity {

    private EditText newWeight, newHeight;
    private TextView bmiValue, txtTodayDate;
    private Button saveButton;
    private Calendar calendar;
    private ImageButton optionsButton;
    private DatabaseHelper databaseHelper;  // Reference to your database helper class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind the options button
        optionsButton = findViewById(R.id.optionsButton);

        // Set OnClickListener to navigate to OptionsActivity
        optionsButton.setOnClickListener(v -> {
            Intent optionsIntent = new Intent(Login.this, optionsActivity.class);
            startActivity(optionsIntent);  // Start OptionsActivity
        });

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Bind views
        newWeight = findViewById(R.id.newWeight);
        newHeight = findViewById(R.id.newHeight);
        bmiValue = findViewById(R.id.bmiValue);
        txtTodayDate = findViewById(R.id.txtTodayDate);
        saveButton = findViewById(R.id.save_Button);

        // Initialize calendar for date picker
        calendar = Calendar.getInstance();
        setupDatePicker();

        // Set up BMI calculation when weight or height is changed
        setupBMICalculation();

        // Handle save button click to save data to DAILY_PROGRESS table
        saveButton.setOnClickListener(v -> saveData());
    }

    // Set up automatic BMI calculation
    private void setupBMICalculation() {
        TextWatcher bmiWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBMI();  // Call this when text changes
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        // Add TextWatcher to both fields to listen for changes
        newWeight.addTextChangedListener(bmiWatcher);
        newHeight.addTextChangedListener(bmiWatcher);
    }

    // Method to calculate and display BMI
    private void calculateBMI() {
        try {
            String weightStr = newWeight.getText().toString();
            String heightStr = newHeight.getText().toString();

            if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                float weight = Float.parseFloat(weightStr);
                float height = Float.parseFloat(heightStr);
                float bmi = weight / (height * height);
                bmiValue.setText(String.format(Locale.getDefault(), "%.2f", bmi));
            } else {
                bmiValue.setText("");
            }
        } catch (NumberFormatException e) {
            bmiValue.setText("");  // Handle invalid input
        }
    }

    // Show date picker when "Select Date" is clicked
    private void setupDatePicker() {
        txtTodayDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(Login.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        txtTodayDate.setText(sdf.format(calendar.getTime()));
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    // Method to save data to DAILY_PROGRESS table
    private void saveData() {
        try {
            // Get user inputs
            String date = txtTodayDate.getText().toString();
            float weight = Float.parseFloat(newWeight.getText().toString());
            float height = Float.parseFloat(newHeight.getText().toString());
            String bmiStr = bmiValue.getText().toString();  // Ensure BMI is calculated

            if (bmiStr.isEmpty() || date.isEmpty()) {
                // Handle invalid data input
                Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float bmi = Float.parseFloat(bmiStr);

            // Save data in the database
            databaseHelper.saveProgressData(date, weight, height, bmi);

            // Show success message
            Toast.makeText(Login.this, "Data successfully saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("LoginActivity", "Error while saving data: " + e.getMessage());
            Toast.makeText(Login.this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }
}
