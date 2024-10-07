package com.vaishnavi.fitflex;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class historyWeights extends AppCompatActivity{
    private ListView historyListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> weightEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_weights);


    }
}
