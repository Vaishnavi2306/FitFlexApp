package com.vaishnavi.fitflex;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class optionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options); // Use your XML layout name here

        TextView accountTextView = findViewById(R.id.account);
        TextView currentWeightsTextView = findViewById(R.id.currentWeights);
        TextView weightHistoryTextView = findViewById(R.id.weightHistory);

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
                Intent intent = new Intent(optionsActivity.this, historyWeights.class);
                startActivity(intent);
            }
        });
    }
}



