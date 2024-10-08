package com.vaishnavi.fitflex;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class welcomeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page); // Ensure this points to your correct XML layout

        dbHelper = new DatabaseHelper(this);

        // TextView for skipping to Login or Main Activity
        TextView skipTextView = findViewById(R.id.skip);

        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user data exists
                if (checkUserData()) {
                    // If data exists, navigate to LoginActivity
                    Intent loginIntent = new Intent(welcomeActivity.this, Login.class);
                    startActivity(loginIntent);
                } else {
                    // If no user data, navigate to MainActivity to allow data entry
                    Intent mainIntent = new Intent(welcomeActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
                finish(); // Optional: finish WelcomeActivity
            }
        });
    }

    private boolean checkUserData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        boolean hasData = cursor.getCount() > 0;
        cursor.close();
        return hasData;
    }
}
