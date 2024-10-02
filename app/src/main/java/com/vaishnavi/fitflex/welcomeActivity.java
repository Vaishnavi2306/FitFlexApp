package com.vaishnavi.fitflex;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class welcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);  // Make sure this points to your correct XML layout

        // Find the TextView by its ID
        TextView skipTextView = findViewById(R.id.skip);

        // Set an onClickListener on the TextView
        skipTextView.setOnClickListener(v -> {
            // Create an intent to navigate to MainActivity
            Intent intent = new Intent(welcomeActivity.this, MainActivity.class);
            startActivity(intent); // Start the MainActivity

        });
    }
}
