package com.vaishnavi.fitflex;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;

public class progressReports extends AppCompatActivity {

    private LineChart bmiChart, weightChart;
    private TextView userName, userWeight, userTargetWeight, userStartDate, userTargetDate, userBMI;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_reports);


        // Initialize the UI elements
        bmiChart = findViewById(R.id.bmiChart);
        weightChart = findViewById(R.id.weightChart);
        userName = findViewById(R.id.userName);
        userWeight = findViewById(R.id.userWeight);
        userTargetWeight = findViewById(R.id.userTargetWeight);
        userStartDate = findViewById(R.id.userStartDate);
        userTargetDate = findViewById(R.id.userTargetDate);
        userBMI = findViewById(R.id.userBMI);

        // Initialize the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Load user details and display them
        loadUserDetails();

        // Load and display data on the charts
        loadChartData();


    }

    // Method to load user details
    // Method to load user details
    private void loadUserDetails() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_DETAILS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Use getColumnIndexOrThrow to ensure correct column retrieval
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));
            float targetWeight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_WEIGHT));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_DATE));
            String targetDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_DATE));
            float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BMI));

            // Setting data to TextViews
            userName.setText(name);
            userWeight.setText(String.format("%.2f kg", weight));
            userTargetWeight.setText(String.format("%.2f kg", targetWeight));
            userStartDate.setText(startDate);
            userTargetDate.setText(targetDate);
            userBMI.setText(String.format("%.2f", bmi));
        }

        if (cursor != null) {
            cursor.close();
        }
    }


    // Method to load and display data on the charts
    private void loadChartData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<Entry> bmiEntries = new ArrayList<>();
        List<Entry> weightEntries = new ArrayList<>();

        // Query the daily_progress table
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_DAILY_PROGRESS, null, null, null, null, null, DatabaseHelper.COLUMN_PROGRESS_DATE + " ASC");

        int index = 1;  // Counter to simulate time (x-axis) for the charts
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESS_DATE));
                float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESS_WEIGHT));
                float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESS_BMI));

                // Add entries to the lists for the charts
                bmiEntries.add(new Entry(index, bmi));
                weightEntries.add(new Entry(index, weight));
                index++;
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        // Set up the BMI chart
        LineDataSet bmiDataSet = new LineDataSet(bmiEntries, "BMI Progress");
        bmiDataSet.setColor(Color.BLUE);
        bmiDataSet.setLineWidth(2f);
        bmiDataSet.setCircleRadius(4f);
        bmiDataSet.setCircleColor(Color.BLUE);

        LineData bmiData = new LineData(bmiDataSet);
        bmiChart.setData(bmiData);
        bmiChart.setScaleEnabled(true);
        bmiChart.setPinchZoom(true);
        bmiChart.setDoubleTapToZoomEnabled(true);
        bmiChart.invalidate();  // Refresh chart

        // Increase X-axis and Y-axis text size
        bmiChart.getXAxis().setTextSize(12f);
        bmiChart.getAxisLeft().setTextSize(12f);
        bmiChart.getAxisRight().setTextSize(12f);
        bmiChart.getDescription().setTextSize(12f);


        weightChart.getXAxis().setTextSize(12f);
        weightChart.getAxisLeft().setTextSize(12f);
        weightChart.getAxisRight().setTextSize(12f);
        weightChart.getDescription().setTextSize(12f);

        // Set up the Weight chart with dark blue line
        LineDataSet weightDataSet = new LineDataSet(weightEntries, "Weight Progress");
        weightDataSet.setColor(Color.BLUE);
        weightDataSet.setLineWidth(2f);
        weightDataSet.setCircleRadius(4f);
        weightDataSet.setCircleColor(Color.BLUE);

        LineData weightData = new LineData(weightDataSet);
        weightChart.setData(weightData);
        weightChart.setScaleEnabled(true);
        weightChart.setPinchZoom(true);
        weightChart.setDoubleTapToZoomEnabled(true);
        weightChart.invalidate();  // Refresh chart
    }
}
