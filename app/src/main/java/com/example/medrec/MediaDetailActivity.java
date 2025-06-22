package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.medrec.adapter.TrendingMediaAdapter;
import com.example.medrec.model.Media;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class MediaDetailActivity extends BaseNoNavActivity {
    private Media currentMedia;

    private ImageView imgCoverLarge;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView textDetailTitle, textDetailDescription;
    private TextView textGenres, textSourceRating;
    private RecyclerView recyclerRecommendations;
    private TrendingMediaAdapter recAdapter;
    private List<Media> recommendationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);
        setPageTitle("Details");

        // 1. Bind views
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        imgCoverLarge = findViewById(R.id.imgCoverLarge);
        textDetailTitle = findViewById(R.id.textDetailTitle);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        textGenres = findViewById(R.id.textGenres);
        textSourceRating = findViewById(R.id.textSourceRating);
        recyclerRecommendations = findViewById(R.id.recyclerRecommendations);
        FloatingActionButton fab = findViewById(R.id.fabAddToLibrary);

        // 2. Toolbar setup
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 3. Get and check the passed Media
        currentMedia = (Media) getIntent().getSerializableExtra("Media");
        if (currentMedia == null) {
            Toast.makeText(this, "No media data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 4. Populate UI
        collapsingToolbar.setTitle(currentMedia.getTitle());
        Glide.with(this)
                .load(currentMedia.getCover())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgCoverLarge);
        textDetailTitle.setText(currentMedia.getTitle());
        textDetailDescription.setText(currentMedia.getDescription());
        // Display genres
        List<String> genres = currentMedia.getCategory();
        textGenres.setText(genres != null ? TextUtils.join(", ", genres) : "N/A");
        // Display source rating
        Double r = currentMedia.getRating();
        textSourceRating.setText(r != null ? String.format(Locale.getDefault(), "%.1f", r) : "N/A");

        // 5. Recommendations carousel
        recAdapter = new TrendingMediaAdapter(this, recommendationList, m -> {
            Intent i = new Intent(this, MediaDetailActivity.class);
            i.putExtra("Media", m);
            startActivity(i);
        });
        recyclerRecommendations.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecommendations.setAdapter(recAdapter);
        fetchRecommendations();

        // 6. FAB: show dialog to add/edit library entry
        fab.setOnClickListener(v -> showAddLibraryDialog());
    }

    private void showAddLibraryDialog() {
        // Inflate dialog view
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_to_library, null);
        RadioGroup rgStatus = view.findViewById(R.id.radioGroupStatus);
        EditText etRating = view.findViewById(R.id.editUserRating);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Add to Library")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    int checkedId = rgStatus.getCheckedRadioButtonId();
                    String status = ((RadioButton) view.findViewById(checkedId)).getText().toString();
                    String ratingStr = etRating.getText().toString().trim();
                    Double userRating = null;
                    if (!ratingStr.isEmpty()) {
                        try { userRating = Double.parseDouble(ratingStr); }
                        catch (NumberFormatException e) { userRating = null; }
                    }
                    saveLibraryEntry(status, userRating);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveLibraryEntry(String status, Double userRating) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String mediaId = currentMedia.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("interactions")
                .child(mediaId);

        Map<String, Object> entry = new HashMap<>();
        entry.put("status", status);
        entry.put("timestamp", System.currentTimeMillis());
        if (userRating != null) entry.put("rating", userRating);

        ref.setValue(entry)
                .addOnSuccessListener(a -> Toast.makeText(this,
                        "Saved to Library!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast
                        .makeText(this, "Save failed.", Toast.LENGTH_SHORT).show());
    }

    private void fetchRecommendations() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Media");
        ref.orderByChild("rating").limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recommendationList.clear();
                        for (DataSnapshot s : snapshot.getChildren()) {
                            Media m = s.getValue(Media.class);
                            if (m != null && !m.getId().equals(currentMedia.getId())) {
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













