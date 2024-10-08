package com.vaishnavi.fitflex;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextView txtUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        databaseHelper = new DatabaseHelper(this);
        txtUserData = findViewById(R.id.txtUserData);

        // Example: Get user details for the user with name "John"
        displayUserDetails("Vaishnavi");
    }

    private void displayUserDetails(String name) {
        Cursor cursor = databaseHelper.getUserData(name);  // Fetch user data from the database

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                StringBuilder userData = new StringBuilder();

                try {
                    userData.append("Name: ").append(cursor.getString(cursor.getColumnIndexOrThrow("name"))).append("\n");
                    userData.append("Age: ").append(cursor.getInt(cursor.getColumnIndexOrThrow("age"))).append("\n");
                    userData.append("Gender: ").append(cursor.getString(cursor.getColumnIndexOrThrow("gender"))).append("\n");
                    userData.append("Date of Birth: ").append(cursor.getString(cursor.getColumnIndexOrThrow("dob"))).append("\n");
                    userData.append("Start Date: ").append(cursor.getString(cursor.getColumnIndexOrThrow("start_date"))).append("\n");
                    userData.append("Target Date: ").append(cursor.getString(cursor.getColumnIndexOrThrow("target_date"))).append("\n");
                    userData.append("Weight: ").append(cursor.getFloat(cursor.getColumnIndexOrThrow("weight"))).append("\n");
                    userData.append("Height: ").append(cursor.getFloat(cursor.getColumnIndexOrThrow("height"))).append("\n");
                    userData.append("Target Weight: ").append(cursor.getFloat(cursor.getColumnIndexOrThrow("target_weight"))).append("\n");
                    userData.append("BMI: ").append(cursor.getFloat(cursor.getColumnIndexOrThrow("bmi"))).append("\n");
                    userData.append("Total Days: ").append(cursor.getLong(cursor.getColumnIndexOrThrow("total_days"))).append("\n");
                } catch (IllegalArgumentException e) {
                    Log.e("AccountActivity", "Error retrieving data from cursor: " + e.getMessage());
                    Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
                }

                txtUserData.setText(userData.toString());  // Set user data to the TextView
            } else {
                Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
                Log.d("AccountActivity", "No data found for user: " + name);
            }
            cursor.close();  // Close cursor after use
        } else {
            Toast.makeText(this, "Error accessing database!", Toast.LENGTH_SHORT).show();
            Log.d("AccountActivity", "Cursor is null");
        }
    }
}
