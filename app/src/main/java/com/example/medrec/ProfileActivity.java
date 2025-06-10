package com.example.medrec;

import android.os.Bundle;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setPageTitle("Profile");

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
}

