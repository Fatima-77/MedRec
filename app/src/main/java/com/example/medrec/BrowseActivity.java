package com.example.medrec;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medrec.adapter.MediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    // RecyclerViews
    private RecyclerView recyclerAnime, recyclerManga, recyclerLightNovels, recyclerBooks;

    // Lists & Adapters
    private final List<Media> animeList       = new ArrayList<>();
    private final List<Media> mangaList       = new ArrayList<>();
    private final List<Media> lightNovelList  = new ArrayList<>();
    private final List<Media> bookList        = new ArrayList<>();

    private MediaAdapter animeAdapter, mangaAdapter, lightNovelAdapter, bookAdapter;

    // Firebase reference
    private DatabaseReference mediaRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // --- RecyclerView bindings ---
        recyclerAnime        = findViewById(R.id.recyclerAnime);
        recyclerManga        = findViewById(R.id.recyclerManga);
        recyclerLightNovels  = findViewById(R.id.recyclerLightNovels);
        recyclerBooks        = findViewById(R.id.recyclerBooks);

        // --- Adapters ---
        animeAdapter      = new MediaAdapter(this, animeList);
        mangaAdapter      = new MediaAdapter(this, mangaList);
        lightNovelAdapter = new MediaAdapter(this, lightNovelList);
        bookAdapter       = new MediaAdapter(this, bookList);

        recyclerAnime.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerManga.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerLightNovels.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerAnime.setAdapter(animeAdapter);
        recyclerManga.setAdapter(mangaAdapter);
        recyclerLightNovels.setAdapter(lightNovelAdapter);
        recyclerBooks.setAdapter(bookAdapter);

        // --- Firebase ---
        mediaRef = FirebaseDatabase.getInstance().getReference("Media");
        loadMediaData();
    }

    private void loadMediaData() {
        mediaRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                animeList.clear();
                mangaList.clear();
                lightNovelList.clear();
                bookList.clear();

                for (DataSnapshot mediaSnap : snapshot.getChildren()) {
                    Media media = mediaSnap.getValue(Media.class);
                    if (media != null && media.getType() != null) {

                        // Debug log
                        Log.d("Firebase", "Fetched media: " + media.getTitle() + " - " + media.getType());

                        // Normalise type text
                        String type = media.getType()
                                .toLowerCase()
                                .replace(" ", "")
                                .replace("_", "");

                        if (type.contains("anime")) {
                            animeList.add(media);
                        } else if (type.contains("manga") || type.contains("manhwa")) {
                            mangaList.add(media);
                        } else if (type.contains("lightnovel")) {
                            lightNovelList.add(media);
                        } else if (type.contains("book")) {
                            bookList.add(media);
                        }
                    }
                }

                // Notify adapters (warnings about efficiency are OK)
                animeAdapter.notifyDataSetChanged();
                mangaAdapter.notifyDataSetChanged();
                lightNovelAdapter.notifyDataSetChanged();
                bookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "loadMediaData cancelled: " + error.getMessage());
            }
        });
    }
}







