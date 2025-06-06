package com.example.medrec;

import android.os.Bundle;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;

import com.example.medrec.R;

public class HomeActivity extends AppCompatActivity {
    RecyclerView mediaListRecycler;
    Spinner statusFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mediaListRecycler = findViewById(R.id.mediaListRecycler);
        statusFilter = findViewById(R.id.statusFilter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: Load user media list (Firebase later)
        // For now, static list can be shown using a RecyclerView adapter
    }
}

