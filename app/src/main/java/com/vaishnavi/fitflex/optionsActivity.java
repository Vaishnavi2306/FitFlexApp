package com.vaishnavi.fitflex;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class optionsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView nameTextView, startDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options); // Use your XML layout name here

        TextView accountTextView = findViewById(R.id.account);
        TextView currentWeightsTextView = findViewById(R.id.currentWeights);
        TextView weightHistoryTextView = findViewById(R.id.weightHistory);
        TextView userDetailsTextView = findViewById(R.id.userdetails);
        TextView progressReportTextView = findViewById(R.id.progress);

        databaseHelper = new DatabaseHelper(this);

        nameTextView = findViewById(R.id.textView4);  // For displaying Name
        startDateTextView = findViewById(R.id.textView8);

        loadUserDetails();
        accountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(optionsActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });



        currentWeightsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(optionsActivity.this, Login.class);
                startActivity(intent);
            }
        });
        weightHistoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(optionsActivity.this, history.class);
                startActivity(intent);
            }
        });
        userDetailsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(optionsActivity.this, UserDetailsActivity.class);
            startActivity(intent);
        });

        progressReportTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(optionsActivity.this, progressReports.class);
                startActivity(intent);
            }
        });
        TextView aboutTextView = findViewById(R.id.about); // Reference to the "About" TextView
        aboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(optionsActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });


    }

    private void loadUserDetails() {
        // Get a readable database
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Query the User_details table to get the name and start date
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME_USER_DETAILS,
                new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_START_DATE},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Fetch the name and start date from the cursor
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_DATE));

                // Set the name to the TextView for name
                nameTextView.setText(name);

                // Create the text "Joined: " with bold style and the date with regular style
                String joinedText = "Joined: ";
                String fullText = joinedText + startDate;

                // Create a SpannableString to apply different styles to different parts of the text
                SpannableString spannableString = new SpannableString(fullText);

                // Make "Joined: " bold
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, joinedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the formatted text to the startDateTextView
                startDateTextView.setText(spannableString);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();  // Close the cursor after use
            }
        } else {
            // If no data found, set default text
            nameTextView.setText("No Name Found");

            // Use SpannableString for the "Joined: " label even if there's no data
            String joinedText = "Joined: ";
            SpannableString spannableString = new SpannableString(joinedText + "No Start Date Found");
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, joinedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            startDateTextView.setText(spannableString);
        }

        // Close the database after the query
        db.close();
    }

}



