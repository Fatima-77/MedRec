package com.example.medrec;

import android.os.Bundle;
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
        Media media = (Media) getIntent().getSerializableExtra("media");

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
            genreView.setText(getString(R.string.genres_format, String.join(", ", media.getGenres())));
            descriptionView.setText(media.getDescription());

            // Load image
            Glide.with(this)
                    .load(media.getImageUrl())
                    .placeholder(R.drawable.splash_background)
                    .into(imageCover);
        }
    }
}



