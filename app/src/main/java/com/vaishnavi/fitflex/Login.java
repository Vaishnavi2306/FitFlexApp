package com.vaishnavi.fitflex;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Login extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 200;
    private EditText newWeight, newHeight;
    private TextView bmiValue, txtTodayDate;
    private Button saveButton, cameraButton;
    private ImageView imageViewPreview;
    private Calendar calendar;
    private ImageButton optionsButton;
    private DatabaseHelper databaseHelper;
    private Bitmap progressImage; // To store the image captured from the camera

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
        cameraButton = findViewById(R.id.cameraButton);
        imageViewPreview = findViewById(R.id.imageViewPreview); // ImageView to show captured image

        // Initialize calendar for date picker
        calendar = Calendar.getInstance();
        setupDatePicker();

        // Set up BMI calculation when weight or height is changed
        setupBMICalculation();

        // Handle save button click to save data to DAILY_PROGRESS table
        saveButton.setOnClickListener(v -> saveData());

        // Handle camera button click to open the camera
        cameraButton.setOnClickListener(v -> checkCameraPermission());
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

    // Check for camera permission at runtime
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            openCamera(); // Open camera if permission already granted
        }
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to open the camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the camera result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            progressImage = (Bitmap) extras.get("data");

            // Show the captured image in the ImageView
            imageViewPreview.setVisibility(View.VISIBLE);
            imageViewPreview.setImageBitmap(progressImage);
        }
    }

    // Method to save data to DAILY_PROGRESS table
    private void saveData() {
        try {
            // Get user inputs
            String date = txtTodayDate.getText().toString();
            float weight = Float.parseFloat(newWeight.getText().toString());
            float height = Float.parseFloat(newHeight.getText().toString());
            String bmiStr = bmiValue.getText().toString();

            if (bmiStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float bmi = Float.parseFloat(bmiStr);

            // Convert Bitmap to byte array if image is available
            byte[] imageBytes = null;
            if (progressImage != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                progressImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageBytes = stream.toByteArray();
            }

            // Save data in the database (including image)
            databaseHelper.saveProgressData(date, weight, height, bmi, imageBytes);

            Toast.makeText(Login.this, "Data and picture saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("LoginActivity", "Error while saving data: " + e.getMessage());
            Toast.makeText(Login.this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }
}
