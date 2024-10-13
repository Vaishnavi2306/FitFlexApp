package com.vaishnavi.fitflex;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Login extends AppCompatActivity {

    private EditText newWeight, newHeight;
    private TextView bmiValue, txtTodayDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind views
        newWeight = findViewById(R.id.newWeight);
        newHeight = findViewById(R.id.newHeight);
        bmiValue = findViewById(R.id.bmiValue);
        txtTodayDate = findViewById(R.id.txtTodayDate);

        // Initialize calendar for date picker
        calendar = Calendar.getInstance();
        setupDatePicker();

        // Set up BMI calculation when weight or height is changed
        setupBMICalculation();
    }

    // Set up automatic BMI calculation
    private void setupBMICalculation() {
        TextWatcher bmiWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBMI();  // Call this when text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        };

        // Add TextWatcher to both fields to listen for changes
        newWeight.addTextChangedListener(bmiWatcher);
        newHeight.addTextChangedListener(bmiWatcher);
    }

    // Calculate and display BMI
    private void calculateBMI() {
        try {
            // Get weight and height values
            String weightStr = newWeight.getText().toString();
            String heightStr = newHeight.getText().toString();

            // Only calculate if both fields are filled
            if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                float weight = Float.parseFloat(weightStr);
                float height = Float.parseFloat(heightStr);

                // Calculate BMI
                float bmi = weight / (height * height);
                bmiValue.setText(String.format(Locale.getDefault(), "%.2f", bmi));  // Display the BMI value
            } else {
                bmiValue.setText("");  // Clear BMI if input is incomplete
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
}
