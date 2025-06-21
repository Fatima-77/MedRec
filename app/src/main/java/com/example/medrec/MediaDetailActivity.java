package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.medrec.adapter.TrendingMediaAdapter;
import com.example.medrec.model.Media;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.*;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaDetailActivity extends AppCompatActivity {
    private ImageView imgCoverLarge;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView textDetailTitle, textDetailDescription;
    private RecyclerView recyclerRecommendations;
    private TrendingMediaAdapter recAdapter;
    private List<Media> recommendationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Views
        collapsingToolbar      = findViewById(R.id.collapsingToolbar);
        imgCoverLarge         = findViewById(R.id.imgCoverLarge);
        textDetailTitle       = findViewById(R.id.textDetailTitle);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        recyclerRecommendations = findViewById(R.id.recyclerRecommendations);

        // Get passed Media
        Media media = (Media) getIntent().getSerializableExtra("Media");
        if (media == null) finish();

        // Set title & image
        collapsingToolbar.setTitle(media.getTitle());
        Glide.with(this)
                .load(media.getCover())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgCoverLarge);

        // Populate details
        textDetailTitle.setText(media.getTitle());
        textDetailDescription.setText(media.getDescription());

        // Recommendations carousel
        recAdapter = new TrendingMediaAdapter(this, recommendationList, m -> {
            Intent i = new Intent(this, MediaDetailActivity.class);
            i.putExtra("Media", m);
            startActivity(i);
        });
        recyclerRecommendations.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecommendations.setAdapter(recAdapter);

        fetchRecommendations();
    }

    private void fetchRecommendations() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Media");
        ref.orderByChild("rating").limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recommendationList.clear();
                        for (DataSnapshot s : snapshot.getChildren()) {
                            Media m = s.getValue(Media.class);
                            if (m != null && !m.getId().equals(
                                    ((Media)getIntent().getSerializableExtra("Media")).getId())) {
                                recommendationList.add(0, m);
                            }
                        }
                        Collections.reverse(recommendationList);
                        recAdapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}







