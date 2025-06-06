package com.example.medrec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

// Firebase imports
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // Declare Firebase database reference
    DatabaseReference databaseReference;

    // Declare button
    Button addMediaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Media");

        Button btnMyLibrary = findViewById(R.id.btnMyLibrary);
        Button btnBrowseMedia = findViewById(R.id.btnBrowseMedia);

        btnMyLibrary.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        });

        btnBrowseMedia.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BrowseActivity.class));
        });

    }
}

