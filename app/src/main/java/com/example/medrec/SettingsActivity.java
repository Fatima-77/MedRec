package com.example.medrec;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setPageTitle("Settings");

        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnAbout  = findViewById(R.id.btnAbout);

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnAbout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("About MediaNest")
                    .setMessage("MediaNest v1.0\n\nBrowse anime, manga, books, and more!\n\n© 2025 YourCompany")
                    .setPositiveButton("OK", (d, which) -> d.dismiss())
                    .show();
        });
    }
}

