package com.example.medrec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends BaseActivity {
    private static final String PREFS_NAME = "mediaNestPrefs";
    private static final String KEY_DARK_MODE = "darkModeEnabled";

    private SharedPreferences prefs;
    private SwitchMaterial darkSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setPageTitle("Settings");

        // Load saved dark-mode preference
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkEnabled = prefs.getBoolean(KEY_DARK_MODE, false);

        // Bind switch and initial state
        darkSwitch = findViewById(R.id.switchDarkMode);
        darkSwitch.setChecked(darkEnabled);

        // Listen for toggle changes
        darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit()
                    .putBoolean(KEY_DARK_MODE, isChecked)
                    .apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Re-create to apply theme
            recreate();
        });

        // Logout and About buttons
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnAbout  = findViewById(R.id.btnAbout);

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnAbout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("About MediaNest")
                        .setMessage("MediaNest v1.0 Browse anime, manga, books, and more! © 2025 YourCompany")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show()
        );
    }
}
