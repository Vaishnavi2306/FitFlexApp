package com.vaishnavi.fitflex;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mDisplayDate, mStartDate, mEndDate, mTotalDays;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private String selectedStartDate, selectedEndDate;

    String[] genderItems = {"Male", "Female", "Non-binary"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> genderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        EditText editTextWeight = findViewById(R.id.weight);
        EditText editTextHeight = findViewById(R.id.height);
        Button buttonCalculateBMI = findViewById(R.id.bmi);
        TextView textViewBMI = findViewById(R.id.bmivalue);
        mTotalDays = findViewById(R.id.totalDays); // Total days TextView

        // BMI calculation on button click
        buttonCalculateBMI.setOnClickListener(view -> {
            String weightInput = editTextWeight.getText().toString().trim();
            String heightInput = editTextHeight.getText().toString().trim();

            if (!weightInput.isEmpty() && !heightInput.isEmpty()) {
                try {
                    float height = Float.parseFloat(heightInput) * 0.3048f; // feet to meters
                    float weight = Float.parseFloat(weightInput);

                    if (height > 0 && weight > 0) {
                        float bmi = weight / (height * height);
                        textViewBMI.setText(String.format("BMI: %.2f", bmi));
                    } else {
                        textViewBMI.setText("Height and weight must be positive numbers.");
                    }
                } catch (NumberFormatException e) {
                    textViewBMI.setText("Invalid number format.");
                }
            } else {
                textViewBMI.setText("Please enter both weight and height.");
            }
        });

        // Set up DatePickers for various TextViews
        mDisplayDate = findViewById(R.id.txtdate);
        mStartDate = findViewById(R.id.txtTodayDate);
        mEndDate = findViewById(R.id.txttarget);

        setupDatePicker(mDisplayDate);
        setupDatePicker(mStartDate);
        setupDatePicker(mEndDate);

        // Set up gender AutoCompleteTextView
        autoCompleteTextView = findViewById(R.id.autoComplete_txt);
        genderAdapter = new ArrayAdapter<>(this, R.layout.list_items, genderItems);
        autoCompleteTextView.setAdapter(genderAdapter);

        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItem = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(MainActivity.this, "Selected Gender: " + selectedItem, Toast.LENGTH_SHORT).show();
        });

        // Handle window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupDatePicker(TextView textView) {
        textView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    MainActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        selectedMonth = selectedMonth + 1; // month is 0-based
                        String date = selectedMonth + "/" + selectedDay + "/" + selectedYear;
                        textView.setText(date);
                        Log.d(TAG, "Selected date: " + date);

                        // Store the selected start and end dates
                        if (textView == mStartDate) {
                            selectedStartDate = date;
                        } else if (textView == mEndDate) {
                            selectedEndDate = date;
                        }

                        // Calculate the total days if both start and end dates are selected
                        if (selectedStartDate != null && selectedEndDate != null) {
                            calculateTotalDays(selectedStartDate, selectedEndDate);
                        }

                    },
                    year, month, day);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        // Handle saving and switching activity
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login.class); // Specify the target activity
            startActivity(intent); // Start the login activity
        });
    }

    // Method to calculate total days between two dates
    private void calculateTotalDays(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            if (start != null && end != null) {
                long differenceInMillis = end.getTime() - start.getTime();
                long daysDifference = differenceInMillis / (1000 * 60 * 60 * 24);

                mTotalDays.setText( + daysDifference +" Days");
            }

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing dates", e);
            mTotalDays.setText("Error calculating days.");
        }
    }
}
