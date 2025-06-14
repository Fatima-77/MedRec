package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.medrec.adapter.TrendingMediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView titleTyping;
    private ViewPager2 carouselViewPager;
    private TrendingMediaAdapter carouselAdapter;
    private List<Media> trendingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleTyping = findViewById(R.id.text_typing_animation);
        TextView subheader = findViewById(R.id.text_subheader);
        Button btnBrowse = findViewById(R.id.btn_browse);
        Button btnLibrary = findViewById(R.id.btn_library);
        carouselViewPager = findViewById(R.id.viewpagercarousel);

        ImageView iconProfile = findViewById(R.id.icon_profile);
        ImageView iconSettings = findViewById(R.id.icon_settings);

        btnBrowse.setOnClickListener(v -> startActivity(new Intent(this, BrowseActivity.class)));
        btnLibrary.setOnClickListener(v -> startActivity(new Intent(this, LibraryActivity.class)));
        iconProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        iconSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        // Typing animation
        startTypingAnimation("Welcome to MediaNest");

        // Set up carousel adapter
        carouselAdapter = new TrendingMediaAdapter(this, trendingList, media -> {
            // Open MediaDetailActivity on poster click
            Intent intent = new Intent(this, MediaDetailActivity.class);
            intent.putExtra("Media", media);
            startActivity(intent);
        });
        carouselViewPager.setAdapter(carouselAdapter);

        // Fetch trending from Firebase
        fetchTrendingMedia();
    }

    // Typing animation
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

    // Fetch trending (top N by popularity/rating)
    private void fetchTrendingMedia() {
        DatabaseReference mediaRef = FirebaseDatabase.getInstance().getReference("Media");
        mediaRef.orderByChild("rating").limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trendingList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Media media = snap.getValue(Media.class);
                            if (media != null) trendingList.add(0, media); // Add to front for descending order
                        }
                        carouselAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}




