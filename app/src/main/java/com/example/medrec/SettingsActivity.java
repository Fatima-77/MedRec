package com.example.medrec;

import android.os.Bundle;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setPageTitle("Settings");

        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
    }
}
