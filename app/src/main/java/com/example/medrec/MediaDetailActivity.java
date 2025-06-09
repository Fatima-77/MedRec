package com.example.medrec;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.medrec.model.Media;

public class MediaDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        // Get the media object
        Media media = (Media) getIntent().getSerializableExtra("Media");

        // View references
        ImageView imageCover = findViewById(R.id.imageCover);
        TextView titleView = findViewById(R.id.textTitle);
        TextView typeView = findViewById(R.id.textType);
        TextView genreView = findViewById(R.id.textGenres);
        TextView descriptionView = findViewById(R.id.textDescription);

        if (media != null) {
            // Set views
            titleView.setText(media.getTitle());
            typeView.setText(getString(R.string.type_format, media.getType()));

            // Join category list to comma-separated string
            String categories = (media.getCategory() != null)
                    ? TextUtils.join(", ", media.getCategory())
                    : "N/A";
            genreView.setText(getString(R.string.genres_format, categories));

            descriptionView.setText(media.getDescription());

            // Load image from cover field
            Glide.with(this)
                    .load(media.getCover())
                    .placeholder(R.drawable.splash_background)
                    .into(imageCover);
        }
    }
}




