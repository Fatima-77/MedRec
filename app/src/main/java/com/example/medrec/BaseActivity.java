package com.example.medrec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected FrameLayout contentFrame;
    protected BottomNavigationView bottomNavigationView;
    protected TextView pageTitleText;
    protected ImageView backButton;
    protected ImageView bannerBookmark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        contentFrame = findViewById(R.id.base_content_frame);
        bottomNavigationView = findViewById(R.id.base_bottom_nav);
        pageTitleText = findViewById(R.id.text_page_title);
        backButton = findViewById(R.id.back_button);

        // Set up back button: finish activity by default
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        bannerBookmark = findViewById(R.id.banner_bookmark);
        if (bannerBookmark != null) {
            bannerBookmark.setVisibility(View.GONE); // hidden by default
        }

        // Apply dynamic status bar padding to the banner
        LinearLayout topBanner = findViewById(R.id.top_banner);
        if (topBanner != null) {
            int statusBarHeight = getStatusBarHeight(this);
            topBanner.setPadding(
                    topBanner.getPaddingLeft(),
                    statusBarHeight,
                    topBanner.getPaddingRight(),
                    topBanner.getPaddingBottom()
            );
        }

        setupBottomNavigation();
    }

    // Let child activities control the icon
    public void showBookmark(boolean show, View.OnClickListener clickListener) {
        if (bannerBookmark != null) {
            bannerBookmark.setVisibility(show ? View.VISIBLE : View.GONE);
            bannerBookmark.setOnClickListener(show ? clickListener : null);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (contentFrame == null) {
            super.setContentView(layoutResID);
        } else {
            LayoutInflater.from(this).inflate(layoutResID, contentFrame, true);
        }
    }

    public void setPageTitle(String title) {
        if (pageTitleText != null) {
            pageTitleText.setText(title);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_library) {
                if (!(this instanceof LibraryActivity)) {
                    startActivity(new Intent(this, LibraryActivity.class));
                    overridePendingTransition(0, 0);
                }
                return true;
            } else if (id == R.id.nav_settings) {
                if (!(this instanceof SettingsActivity)) {
                    startActivity(new Intent(this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
                }
                return true;
            } else if (id == R.id.nav_profile) {
                if (!(this instanceof ProfileActivity)) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                }
                return true;
            } else if (id == R.id.base_bottom_nav) {
                if (!(this instanceof BrowseActivity)) {
                    startActivity(new Intent(this, BrowseActivity.class));
                    overridePendingTransition(0, 0);
                }
                return true;
            }
            return false;
        });
    }

    // Helper for dynamic status bar height
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}




