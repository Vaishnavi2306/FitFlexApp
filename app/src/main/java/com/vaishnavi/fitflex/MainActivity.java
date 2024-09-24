package com.vaishnavi.fitflex;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mDisplayDate, mStartDate, mEndDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String[] item = {"Male", "Female", "Non-binary"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        EditText editTextWeight = findViewById(R.id.weight);
        EditText editTextHeight = findViewById(R.id.height);
        Button button = findViewById(R.id.bmi);
        TextView textView = findViewById(R.id.bmivalue);

        // BMI calculation on button click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input values
                String weightInput = editTextWeight.getText().toString().trim();
                String heightInput = editTextHeight.getText().toString().trim();

                // Validate inputs
                if (!weightInput.isEmpty() && !heightInput.isEmpty()) {
                    try {
                        // Convert height from feet to meters (1 foot = 0.3048 meters)
                        float height = Float.parseFloat(heightInput) * 0.3048f;
                        float weight = Float.parseFloat(weightInput);

                        if (height > 0 && weight > 0) {
                            // Calculate BMI
                            float bmi = weight / (height * height);
                            textView.setText(String.format("BMI: %.2f", bmi));
                        } else {
                            textView.setText("Height and weight must be positive numbers.");
                        }
                    } catch (NumberFormatException e) {
                        textView.setText("Invalid number format.");
                    }
                } else {
                    // Handle empty input fields
                    textView.setText("Please enter both weight and height.");
                }
            }
        });

        mDisplayDate = findViewById(R.id.txtdate);
        mStartDate = findViewById(R.id.txtStartDate);
        mEndDate = findViewById(R.id.txttarget);

        // Set up date picker listeners
        setupDatePicker(mDisplayDate);
        setupDatePicker(mStartDate);
        setupDatePicker(mEndDate);

        autoCompleteTextView = findViewById(R.id.autoComplete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_items, item);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, "Item= " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupDatePicker(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }
}
