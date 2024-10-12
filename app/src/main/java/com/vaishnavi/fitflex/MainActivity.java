package com.vaishnavi.fitflex;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText dobEditText, startDateEditText, targetDateEditText;
    private EditText heightEditText, weightEditText, bmiEditText, totalDaysEditText, targetWeightEditText;
    private String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this points to your correct XML layout

        dbHelper = new DatabaseHelper(this);

        EditText nameEditText = findViewById(R.id.et_name);
        EditText ageEditText = findViewById(R.id.et_age);
        dobEditText = findViewById(R.id.et_dob);
        startDateEditText = findViewById(R.id.start_date);
        targetDateEditText = findViewById(R.id.target_date);
        heightEditText = findViewById(R.id.et_height);
        weightEditText = findViewById(R.id.et_weight);
        bmiEditText = findViewById(R.id.et_bmi);
        totalDaysEditText = findViewById(R.id.total_days);
        targetWeightEditText = findViewById(R.id.et_target_weight); // Added target weight field
        Button saveButton = findViewById(R.id.btn_save);

        // Gender dropdown
        AutoCompleteTextView genderDropdown = findViewById(R.id.gender_dropdown);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_dropdown_item_1line);

// Set the adapter to the AutoCompleteTextView
        genderDropdown.setAdapter(adapter);

// Set the item click listener
        genderDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
            }
        });


        // Date pickers
        dobEditText.setOnClickListener(v -> showDatePicker(dobEditText));
        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        targetDateEditText.setOnClickListener(v -> showDatePicker(targetDateEditText));

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            int age = Integer.parseInt(ageEditText.getText().toString());
            float weight = Float.parseFloat(weightEditText.getText().toString());
            float height = Float.parseFloat(heightEditText.getText().toString());
            float targetWeight = Float.parseFloat(targetWeightEditText.getText().toString()); // Get target weight

            // Calculate BMI
            float bmi = calculateBMI(weight, height);
            bmiEditText.setText(String.format("%.2f", bmi));

            // Calculate total days
            int totalDays = calculateTotalDays(startDateEditText.getText().toString(), targetDateEditText.getText().toString());
            totalDaysEditText.setText(String.valueOf(totalDays));

            saveUserData(name, age, selectedGender, dobEditText.getText().toString(),
                    startDateEditText.getText().toString(), targetDateEditText.getText().toString(),
                    weight, height, targetWeight, bmi, totalDays); // Save target weight
        });
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editText.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private float calculateBMI(float weight, float height) {
        // Height is in cm, convert to meters for BMI calculation
        height = height / 100;
        return weight / (height * height);
    }

    private int calculateTotalDays(String startDate, String targetDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date start = format.parse(startDate);
            Date target = format.parse(targetDate);
            if (start != null && target != null) {
                long differenceInMillis = target.getTime() - start.getTime();
                return (int) (differenceInMillis / (1000 * 60 * 60 * 24)); // Convert milliseconds to days
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0; // Default to 0 if there's an error
    }

    public void saveUserData(String name, int age, String gender, String dob, String startDate, String targetDate, float weight, float height, float targetWeight, float bmi, int totalDays) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_AGE, age);
        values.put(DatabaseHelper.COLUMN_GENDER, gender);
        values.put(DatabaseHelper.COLUMN_DOB, dob);
        values.put(DatabaseHelper.COLUMN_START_DATE, startDate);
        values.put(DatabaseHelper.COLUMN_TARGET_DATE, targetDate);
        values.put(DatabaseHelper.COLUMN_WEIGHT, weight);
        values.put(DatabaseHelper.COLUMN_HEIGHT, height);
        values.put(DatabaseHelper.COLUMN_BMI, bmi);
        values.put(DatabaseHelper.COLUMN_TOTAL_DAYS, totalDays);
        values.put(DatabaseHelper.COLUMN_TARGET_WEIGHT, targetWeight); // Save target weight

        long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Error saving user data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User data saved with ID: " + newRowId, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, welcomeActivity.class); // Ensure WelcomeActivity is the correct class
            startActivity(intent);
            finish(); // Close MainActivity
        }
    }
}
