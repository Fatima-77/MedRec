package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected FrameLayout contentFrame;
    protected BottomNavigationView bottomNavigationView;
    protected TextView appNameText, pageTitleText;
    protected TextView appNameText, pageTitleText;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        contentFrame = findViewById(R.id.base_content_frame);
        bottomNavigationView = findViewById(R.id.base_bottom_nav);
        appNameText = findViewById(R.id.app_name_text);
        pageTitleText = findViewById(R.id.page_title_text);

        setupBottomNavigation();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View root = getLayoutInflater().inflate(R.layout.activity_base, null);
        super.setContentView(root);

        // Setup references after base layout is set
        appNameText = root.findViewById(R.id.text_app_name);
        pageTitleText = root.findViewById(R.id.text_page_title);
        bottomNavigationView = root.findViewById(R.id.base_bottom_nav);

        if (contentFrame != null) {
            LayoutInflater.from(this).inflate(layoutResID, contentFrame, true);
        }
    }

    protected void setPageTitle(String title) {
        if (pageTitleText != null) {
            pageTitleText.setVisibility(View.VISIBLE);
            pageTitleText.setText(title);
        }
    }

    protected void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home && !(getClass().equals(MainActivity.class))) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_library && !(getClass().equals(LibraryActivity.class))) {
                startActivity(new Intent(this, LibraryActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile && !(getClass().equals(ProfileActivity.class))) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_settings && !(getClass().equals(SettingsActivity.class))) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}

