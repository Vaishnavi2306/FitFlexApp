package com.vaishnavi.fitflex;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class welcomeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page); // Your welcome layout

        dbHelper = new DatabaseHelper(this);

        TextView skipTextView = findViewById(R.id.skip); // Assuming there's a "skip" button or TextView

        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkUserData()) {
                        // If data exists, navigate to LoginActivity for daily entry
                        Intent loginIntent = new Intent(welcomeActivity.this, Login.class);
                        startActivity(loginIntent);
                    } else {
                        // If no data, navigate to MainActivity to enter user details
                        Intent mainIntent = new Intent(welcomeActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                    finish(); // Optional: finish WelcomeActivity
                } catch (Exception e) {
                    Log.e("WelcomeActivity", "Error on skip button: " + e.getMessage());
                    Toast.makeText(welcomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Check if user data exists in the new table user_details
    private boolean checkUserData() {
        // Get a readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query the user_details table to check if any data exists
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_DETAILS, null, null, null, null, null, null);

        // Check if the table has any rows (data)
        boolean hasData = cursor.getCount() > 0; // Data exists if count > 0

        // Clean up the cursor and close the database connection
        cursor.close();
        db.close();

        return hasData;  // Return true if data exists, false otherwise
    }

}
