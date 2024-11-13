package com.vaishnavi.fitflex;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    private EditText newWeight, newHeight;
    private TextView bmiValue, txtTodayDate;
    private Button saveButton, cameraButton;
    private ImageView imageViewPreview;
    private Calendar calendar;
    private ImageButton optionsButton;
    private DatabaseHelper databaseHelper;
    private Bitmap progressImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        optionsButton = findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(v -> {
            Intent optionsIntent = new Intent(Login.this, optionsActivity.class);
            startActivity(optionsIntent);
        });

        databaseHelper = new DatabaseHelper(this);

        newWeight = findViewById(R.id.newWeight);
        newHeight = findViewById(R.id.newHeight);
        newHeight.setEnabled(false); // Disable editing for height field

        bmiValue = findViewById(R.id.bmiValue);
        txtTodayDate = findViewById(R.id.txtTodayDate);
        saveButton = findViewById(R.id.save_Button);
        cameraButton = findViewById(R.id.cameraButton);
        imageViewPreview = findViewById(R.id.imageViewPreview);

        calendar = Calendar.getInstance();
        setupDatePicker();

        setDefaultHeight(); // Set the default height from the database

        setupBMICalculation();

        saveButton.setOnClickListener(v -> saveData());
        cameraButton.setOnClickListener(v -> checkCameraPermission());
    }

    private void setupBMICalculation() {
        TextWatcher bmiWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBMI();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        newWeight.addTextChangedListener(bmiWatcher);
    }

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
            bmiValue.setText("");
        }
    }

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

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

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

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            progressImage = (Bitmap) extras.get("data");
            imageViewPreview.setImageBitmap(progressImage);
        }
    }

    private void saveData() {
        try {
            String date = txtTodayDate.getText().toString();
            float weight = Float.parseFloat(newWeight.getText().toString());
            float height = Float.parseFloat(newHeight.getText().toString());
            String bmiStr = bmiValue.getText().toString();

            if (bmiStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float bmi = Float.parseFloat(bmiStr);

            if (isDateAlreadyEntered(date)) {
                Toast.makeText(Login.this, "Data for this date already exists. Please delete it first.", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] imageBytes = null;
            if (progressImage != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                progressImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageBytes = stream.toByteArray();
            }

            databaseHelper.saveProgressData(date, weight, height, bmi, imageBytes);
            Toast.makeText(Login.this, "Data and picture saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("LoginActivity", "Error while saving data: " + e.getMessage());
            Toast.makeText(Login.this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDateAlreadyEntered(String date) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DAILY_PROGRESS,
                new String[]{DatabaseHelper.COLUMN_PROGRESS_DATE},
                DatabaseHelper.COLUMN_PROGRESS_DATE + " = ?",
                new String[]{date},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void setDefaultHeight() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_DETAILS,
                new String[]{DatabaseHelper.COLUMN_HEIGHT},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                float height = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT));
                newHeight.setText(String.valueOf(height));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            newHeight.setText("");
        }

        db.close();
    }
}
