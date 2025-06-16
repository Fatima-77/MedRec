package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.medrec.adapter.TrendingMediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView titleTyping;
    private ViewPager2 carouselViewPager;
    private TrendingMediaAdapter carouselAdapter;
    private List<Media> trendingList = new ArrayList<>();

    // Utility: Get status bar height in pixels
    public static int getStatusBarHeight(android.content.Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleTyping = findViewById(R.id.text_typing_animation);
        Button btnBrowse = findViewById(R.id.btn_browse);
        Button btnLibrary = findViewById(R.id.btn_library);
        carouselViewPager = findViewById(R.id.viewpager_carousel);

        ImageView iconProfile = findViewById(R.id.icon_profile);
        ImageView iconSettings = findViewById(R.id.icon_settings);

        btnBrowse.setOnClickListener(v -> startActivity(new Intent(this, BrowseActivity.class)));
        btnLibrary.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));
        iconProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        iconSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        LinearLayout mainBanner = findViewById(R.id.main_banner);
        if (mainBanner != null) {
            int statusBarHeight = getStatusBarHeight(this);
            mainBanner.setPadding(
                    mainBanner.getPaddingLeft(),
                    statusBarHeight,
                    mainBanner.getPaddingRight(),
                    mainBanner.getPaddingBottom()
            );
        }

        // Typing animation
        startTypingAnimation("Welcome to MediaNest");

        // Set up carousel adapter
        carouselAdapter = new TrendingMediaAdapter(this, trendingList, media -> {
            Intent intent = new Intent(this, MediaDetailActivity.class);
            intent.putExtra("Media", media);
            startActivity(intent);
        });
        carouselViewPager.setAdapter(carouselAdapter);

        // Make carousel dynamic: peek effect + transformer
        carouselViewPager.setOffscreenPageLimit(3);
        carouselViewPager.setClipToPadding(false);
        carouselViewPager.setPadding(80, 0, 80, 0); // adjust for more/less peek

        carouselViewPager.setPageTransformer((page, position) -> {
            float scale = Math.max(0.9f, 1 - Math.abs(position) * 0.1f);
            page.setScaleY(scale);
            page.setScaleX(scale);
            page.setAlpha(0.6f + (scale - 0.9f));
        });

        // Fetch trending from Firebase
        fetchTrendingMedia();
    }

    private void startTypingAnimation(String text) {
        Handler handler = new Handler();
        final int[] i = {0};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (i[0] <= text.length()) {
                    titleTyping.setText(text.substring(0, i[0]));
                    i[0]++;
                    handler.postDelayed(this, 45);
                }
            }
        };
        handler.post(runnable);
    }

    private void fetchTrendingMedia() {
        DatabaseReference mediaRef = FirebaseDatabase.getInstance().getReference("Media");
        mediaRef.orderByChild("rating").limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trendingList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Media media = snap.getValue(Media.class);
                            if (media != null) trendingList.add(media); // add for correct order
                        }
                        Collections.reverse(trendingList); // highest rating first
                        carouselAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}





