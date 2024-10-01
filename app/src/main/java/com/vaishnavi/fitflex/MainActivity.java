package com.vaishnavi.fitflex;

import static com.vaishnavi.fitflex.R.id.totalDays;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mDisplayDate, mStartDate, mEndDate;
    private TextView  mTotalDays;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

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

        // Initialize date display and total days TextView
        mTotalDays = findViewById(R.id.totalDays);
        mDisplayDate = findViewById(R.id.txtdate);
        mStartDate = findViewById(R.id.txtTodayDate);
        mEndDate = findViewById(R.id.txttarget);

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

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            // Check all required fields
            if (areFieldsValid(editTextWeight, editTextHeight, mStartDate, mEndDate, autoCompleteTextView)) {
                String weight = editTextWeight.getText().toString().trim();
                String height = editTextHeight.getText().toString().trim();
                String gender = autoCompleteTextView.getText().toString().trim();
                String startDate = mStartDate.getText().toString().trim();
                String endDate = mEndDate.getText().toString().trim();

                // Save data
                saveData(weight, height, gender, startDate, endDate);

                // Calculate total days and display
                int totalDaysCount = calculateTotalDays(startDate, endDate);
                mTotalDays.setText("Total Days: " + totalDaysCount);

                // Proceed to the next activity
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveData(String weight, String height, String gender, String startDate, String endDate) {
        SharedPreferences sharedPreferences = getSharedPreferences("FITFLEX_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("weight", weight);
        editor.putString("height", height);
        editor.putString("gender", gender);
        editor.putString("startDate", startDate);
        editor.putString("endDate", endDate);
        editor.apply(); // Commit changes
    }

    private boolean areFieldsValid(EditText weight, EditText height, TextView startDate, TextView endDate, AutoCompleteTextView gender) {
        return !weight.getText().toString().trim().isEmpty() &&
                !height.getText().toString().trim().isEmpty() &&
                !startDate.getText().toString().trim().isEmpty() &&
                !endDate.getText().toString().trim().isEmpty() &&
                !gender.getText().toString().trim().isEmpty();
    }

    private int calculateTotalDays(String startDateStr, String endDateStr) {
        String[] startParts = startDateStr.split("/");
        String[] endParts = endDateStr.split("/");

        // Check if date format is valid
        if (startParts.length != 3 || endParts.length != 3) {
            return 0; // Invalid date format
        }

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.YEAR, Integer.parseInt(startParts[2]));
        startCalendar.set(Calendar.MONTH, Integer.parseInt(startParts[0]) - 1); // month is 0-based
        startCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startParts[1]));

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.YEAR, Integer.parseInt(endParts[2]));
        endCalendar.set(Calendar.MONTH, Integer.parseInt(endParts[0]) - 1); // month is 0-based
        endCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endParts[1]));

        long diffInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return (int) (diffInMillis / (1000 * 60 * 60 * 24)); // Convert milliseconds to days
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
                    },
                    year, month, day);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }
}
