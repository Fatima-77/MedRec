package com.example.medrec;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.medrec.model.Media;
import com.example.medrec.model.LibraryEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MediaDetailActivity extends AppCompatActivity {

    private Media media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_media_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_library) {
            showStatusDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                        saveMediaToLibrary(selectedStatus);
                    }
                })
                .show();
    }

    private void saveMediaToLibrary(String status) {
        if (media == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userLibRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId).child("Library").child(media.getId());

        userLibRef.setValue(new LibraryEntry(media, status));
        Toast.makeText(this, "Added to Library as " + status, Toast.LENGTH_SHORT).show();
    }

    private void removeFromLibrary() {
        if (media == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userLibRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId).child("Library").child(media.getId());

        userLibRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Removed from Library", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove", Toast.LENGTH_SHORT).show());
    }

}





