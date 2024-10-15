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
        setContentView(R.layout.welcome_page);

        dbHelper = new DatabaseHelper(this);

        TextView skipTextView = findViewById(R.id.skip);

        skipTextView.setOnClickListener(v -> {
            if (checkUserDataExists()) {
                // If data exists, navigate to LoginActivity
                Intent intent = new Intent(welcomeActivity.this, Login.class);
                startActivity(intent);
            } else {
                // If no data, navigate to MainActivity to enter user details
                Intent intent = new Intent(welcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }

    // Check if user data exists in the user_details table
    private boolean checkUserDataExists() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME_USER_DETAILS, null); // Check all records
        boolean hasData = cursor.getCount() > 0; // Return true if user data exists
        cursor.close();
        db.close();
        return hasData;
    }

}
