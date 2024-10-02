package com.vaishnavi.fitflex;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar; // Make sure to use the right Calendar import
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText weightEditText;
    private EditText heightEditText;
    private TextView bmiValueTextView;
    private TextView mDisplayDate; // Add TextView for date display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure this matches your layout file

        ImageButton optionsButton = findViewById(R.id.optionsButton);

        // Set up click listener for the optionsButton
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to OptionsActivity
                Intent intent = new Intent(Login.this, optionsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize UI elements
        weightEditText = findViewById(R.id.newWeight);
        heightEditText = findViewById(R.id.newHeight);
        bmiValueTextView = findViewById(R.id.bmiValue); // TextView to display BMI
        mDisplayDate = findViewById(R.id.txtTodayDate); // Initialize the date TextView

        // Set the current date when the activity starts
        setCurrentDate();

        // Set up click listener to show date picker
        mDisplayDate.setOnClickListener(v -> showDatePickerDialog());

        // Add text watchers for the EditTexts
        weightEditText.addTextChangedListener(bmiTextWatcher);
        heightEditText.addTextChangedListener(bmiTextWatcher);
    }

    private final TextWatcher bmiTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No action needed before text changes
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            calculateBMI(); // Calculate BMI on text change
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed after text changes
        }
    };

    private void calculateBMI() {
        String weightStr = weightEditText.getText().toString();
        String heightStr = heightEditText.getText().toString();

        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            double weight = Double.parseDouble(weightStr); // Get weight in kg
            double height = Double.parseDouble(heightStr); // Get height in ft

            // Convert height from feet to meters (1 foot = 0.3048 meters)
            height = height * 0.3048;

            // Calculate BMI: weight (kg) / (height (m) * height (m))
            double bmi = weight / (height * height);
            bmiValueTextView.setText(String.format("BMI: %.2f", bmi)); // Display the BMI value
        } else {
            bmiValueTextView.setText("BMI: "); // Clear the BMI text if fields are empty
        }
    }

    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        mDisplayDate.setText(day + "/" + (month + 1) + "/" + year); // Month is 0-based
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(Login.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the TextView with the selected date
                    updateDateTextView(selectedDay, selectedMonth, selectedYear);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Helper method to update the TextView with the selected date
    private void updateDateTextView(int day, int month, int year) {
        mDisplayDate.setText(day + "/" + (month + 1) + "/" + year); // Month is 0-based
    }
}