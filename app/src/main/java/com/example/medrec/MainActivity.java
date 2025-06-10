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
    private Button browseButton, libraryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Not extending BaseActivity

        browseButton = findViewById(R.id.btn_browse_media);
        libraryButton = findViewById(R.id.btn_my_library);

        browseButton.setOnClickListener(v -> startActivity(new Intent(this, BrowseActivity.class)));
        libraryButton.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));

        // Settings icon click
        findViewById(R.id.settings_icon).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }
}


