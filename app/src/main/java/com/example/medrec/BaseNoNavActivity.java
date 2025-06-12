package com.example.medrec;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseNoNavActivity extends AppCompatActivity {

    protected FrameLayout contentFrame;
    protected TextView pageTitleText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_no_nav);

        contentFrame = findViewById(R.id.base_content_frame);
        pageTitleText = findViewById(R.id.text_page_title);

        // --- DYNAMIC BANNER PADDING ---
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
    }

    // Allows child activities to inflate their own layout into the frame
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

    // --- DYNAMIC STATUS BAR HEIGHT HELPER ---
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

