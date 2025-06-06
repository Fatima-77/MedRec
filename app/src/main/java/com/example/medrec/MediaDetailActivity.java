package com.example.medrec;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.medrec.model.Media;

public class MediaDetailActivity extends AppCompatActivity {

    private TextView titleView, typeView, genreView, languageView, descriptionView;
    private ImageView coverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        titleView = findViewById(R.id.titleView);
        typeView = findViewById(R.id.typeView);
        genreView = findViewById(R.id.genreView);
        languageView = findViewById(R.id.languageView);
        descriptionView = findViewById(R.id.descriptionView);
        coverImage = findViewById(R.id.coverImage);

        Media media = (Media) getIntent().getSerializableExtra("media");

        if (media != null) {
            titleView.setText(media.getTitle());
            typeView.setText("Type: " + media.getType());
            genreView.setText("Genres: " + media.getGenres());
            languageView.setText("Language: " + media.getLanguage());
            descriptionView.setText(media.getDescription());
            Glide.with(this).load(media.getImageUrl()).into(coverImage);
        }
    }
}


