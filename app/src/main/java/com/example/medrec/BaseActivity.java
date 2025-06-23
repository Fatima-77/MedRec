package com.example.medrec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected FrameLayout contentFrame;
    protected BottomNavigationView bottomNav;
    protected TextView pageTitleText;
    protected ImageView backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        contentFrame      = findViewById(R.id.base_content_frame);
        bottomNav         = findViewById(R.id.base_bottom_nav);
        pageTitleText     = findViewById(R.id.text_page_title);
        backButton        = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        // status bar padding
        LinearLayout topBanner = findViewById(R.id.top_banner);
        int statusBarH = getStatusBarHeight(this);
        topBanner.setPadding(
                topBanner.getPaddingLeft(),
                statusBarH,
                topBanner.getPaddingRight(),
                topBanner.getPaddingBottom()
        );

        setupBottomNav();
        highlightCurrentItem();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (contentFrame == null) {
            super.setContentView(layoutResID);
        } else {
            LayoutInflater.from(this)
                    .inflate(layoutResID, contentFrame, true);
        }
    }

    public void setPageTitle(String title) {
        pageTitleText.setText(title);
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Class<?> cls = this.getClass();

            if (id == R.id.nav_home) {
                if (!cls.equals(MainActivity.class)) {
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0,0);
                }
            }
            else if (id == R.id.nav_browse) {
                if (!cls.equals(BrowseActivity.class)) {
                    startActivity(new Intent(this, BrowseActivity.class));
                    overridePendingTransition(0,0);
                }
            }
            else if (id == R.id.nav_library) {
                if (!cls.equals(LibraryActivity.class)) {
                    startActivity(new Intent(this, LibraryActivity.class));
                    overridePendingTransition(0,0);
                }
            }
            else if (id == R.id.nav_profile) {
                if (!cls.equals(ProfileActivity.class)) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(0,0);
                }
            }
            else if (id == R.id.nav_settings) {
                if (!cls.equals(SettingsActivity.class)) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    overridePendingTransition(0,0);
                }
            }
            return true;
        });
    }

    private void highlightCurrentItem() {
        Class<?> cls = this.getClass();

        int pick;
        if (cls.equals(MainActivity.class))        pick = R.id.nav_home;
        else if (cls.equals(BrowseActivity.class)) pick = R.id.nav_browse;
        else if (cls.equals(LibraryActivity.class))pick = R.id.nav_library;
        else if (cls.equals(ProfileActivity.class))pick = R.id.nav_profile;
        else if (cls.equals(SettingsActivity.class))pick = R.id.nav_settings;
        else {
            // RecommendationsActivity (or any other) → do not select anything
            return;
        }

        bottomNav.setSelectedItemId(pick);
    }

    public static int getStatusBarHeight(Context ctx) {
        int resId = ctx.getResources().getIdentifier(
                "status_bar_height","dimen","android");
        return resId>0
                ? ctx.getResources().getDimensionPixelSize(resId)
                : 0;
    }
}
