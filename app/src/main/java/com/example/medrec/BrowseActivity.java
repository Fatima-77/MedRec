package com.example.medrec;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medrec.adapter.MediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

public class BrowseActivity extends AppCompatActivity {

    private RecyclerView recyclerAnime, recyclerManga, recyclerLightNovels, recyclerBooks;
    private MediaAdapter animeAdapter, mangaAdapter, lightNovelAdapter, bookAdapter;
    private List<Media> animeList, mangaList, lightNovelList, bookList;
    private DatabaseReference mediaRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerAnime = findViewById(R.id.recyclerAnime);
        recyclerManga = findViewById(R.id.recyclerManga);
        recyclerLightNovels = findViewById(R.id.recyclerLightNovels);
        recyclerBooks = findViewById(R.id.recyclerBooks);

        animeList = new ArrayList<>();
        mangaList = new ArrayList<>();
        lightNovelList = new ArrayList<>();
        bookList = new ArrayList<>();

        animeAdapter = new MediaAdapter(this, animeList);
        mangaAdapter = new MediaAdapter(this, mangaList);
        lightNovelAdapter = new MediaAdapter(this, lightNovelList);
        bookAdapter = new MediaAdapter(this, bookList);

        recyclerAnime.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerManga.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerLightNovels.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerAnime.setAdapter(animeAdapter);
        recyclerManga.setAdapter(mangaAdapter);
        recyclerLightNovels.setAdapter(lightNovelAdapter);
        recyclerBooks.setAdapter(bookAdapter);

        mediaRef = FirebaseDatabase.getInstance().getReference("media");

        loadMediaData();
    }

    private void loadMediaData() {
        mediaRef.addValueEventListener(new ValueEventListener() {
            @Override
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot mediaSnapshot : snapshot.getChildren()) {
                    Media media = mediaSnapshot.getValue(Media.class);
                    if (media != null) {
                        // ✅ Add this line to log fetched media
                        Log.d("Firebase", "Fetched media: " + media.getTitle() + " - " + media.getType());

                        String type = media.getType().toLowerCase().replace(" ", "").replace("_", "");
                        if (type.contains("anime")) {
                            animeList.add(media);
                        } else if (type.contains("manga")) {
                            mangaList.add(media);
                        } else if (type.contains("lightnovel")) {
                            lightNovelList.add(media);
                        } else if (type.contains("book")) {
                            bookList.add(media);
                        }
                    }
                }

                animeAdapter.notifyDataSetChanged();
                mangaAdapter.notifyDataSetChanged();
                lightNovelAdapter.notifyDataSetChanged();
                bookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log or show a toast
            }
        });
    }
}






