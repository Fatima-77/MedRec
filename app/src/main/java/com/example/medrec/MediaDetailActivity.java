package com.example.medrec;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.medrec.model.Media;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.EditText;
import android.text.InputType;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseUser;
import android.view.View;

public class MediaDetailActivity extends BaseActivity {

    private Media media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);
        setPageTitle("Media Details");

        // Show bookmark icon in the banner and attach click logic
        showBookmark(true, v -> showStatusDialog());

        // Get the media object
        media = (Media) getIntent().getSerializableExtra("Media");

        // View references
        ImageView imageCover = findViewById(R.id.imageCover);
        TextView titleView = findViewById(R.id.textTitle);
        TextView typeView = findViewById(R.id.textType);
        TextView genreView = findViewById(R.id.textGenres);
        TextView descriptionView = findViewById(R.id.textDescription);

        if (media != null) {
            titleView.setText(media.getTitle());
            typeView.setText(getString(R.string.type_format, media.getType()));

            String categories = (media.getCategory() != null)
                    ? TextUtils.join(", ", media.getCategory())
                    : "N/A";
            genreView.setText(getString(R.string.genres_format, categories));

            descriptionView.setText(media.getDescription());

            Glide.with(this)
                    .load(media.getCover())
                    .placeholder(R.drawable.splash_background)
                    .into(imageCover);
        }
    }

    // No options menu, no Toolbar

    private void showStatusDialog() {
        String[] options = {
                "Watching/Reading", "Plan to Watch/Read", "Completed", "Dropped", "Remove from Library"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Library Status")
                .setItems(options, (dialog, which) -> {
                    String selectedStatus = options[which];
                    if (selectedStatus.equals("Remove from Library")) {
                        removeFromLibrary();
                    } else {
                        // Ask for optional rating if not "planned"
                        if (selectedStatus.contains("Completed") || selectedStatus.contains("Watching")) {
                            showRatingDialog(selectedStatus);
                        } else {
                            logUserInteraction(selectedStatus, null); // no rating
                        }
                    }
                })
                .show();
    }

    private void saveMediaToLibrary(String status) {
        logUserInteraction(status, null);
    }

    private void removeFromLibrary() {
        logUserInteraction("remove", null);
    }

    private void logUserInteraction(String status, @Nullable Double rating) {
        if (media == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String uid = currentUser.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("interactions")
                .child(media.getId());

        if (status.equals("remove")) {
            ref.removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Removed from Library", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove", Toast.LENGTH_SHORT).show());
        } else {
            String normalized = convertStatus(status);

            HashMap<String, Object> data = new HashMap<>();
            data.put("status", normalized);
            data.put("timestamp", System.currentTimeMillis());
            if (rating != null) {
                data.put("rating", rating);
            }

            ref.setValue(data);
            Toast.makeText(this, "Saved as " + status, Toast.LENGTH_SHORT).show();
        }
    }

    private String convertStatus(String input) {
        input = input.toLowerCase();
        if (input.contains("watching") || input.contains("reading")) return "watching/reading";
        if (input.contains("plan")) return "planned";
        if (input.contains("completed")) return "completed";
        if (input.contains("dropped")) return "dropped";
        return "planned";
    }

    private void showRatingDialog(String status) {
        final EditText input = new EditText(this);
        input.setHint("Enter rating (1.0 - 10.0)");
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Optional Rating")
                .setMessage("Enter your rating for this media (leave blank to skip):")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String inputText = input.getText().toString().trim();
                    Double rating = null;
                    if (!inputText.isEmpty()) {
                        try {
                            rating = Double.parseDouble(inputText);
                            if (rating < 1.0 || rating > 10.0) {
                                Toast.makeText(this, "Rating must be 1.0 to 10.0", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid rating", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    logUserInteraction(status, rating);
                })
                .setNegativeButton("Skip", (dialog, which) -> logUserInteraction(status, null))
                .show();
    }
}






