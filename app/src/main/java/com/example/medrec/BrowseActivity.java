package com.example.medrec;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class BrowseActivity extends BaseActivity {

    private Spinner spinnerType;
    private RecyclerView recyclerMedia;

    private final List<Media> mediaList = new ArrayList<>();
    private MediaAdapter mediaAdapter;
    private DatabaseReference mediaRef;

    private final String[] mediaTypes = {"Anime", "Manga/Manhwa", "Light Novel", "Book"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        setPageTitle("Browse");

        // Initialize views
        spinnerType = findViewById(R.id.spinnerType);
        recyclerMedia = findViewById(R.id.recyclerMedia);

        // Set up RecyclerView
        recyclerMedia.setLayoutManager(new GridLayoutManager(this, 2));
        mediaAdapter = new MediaAdapter(this, mediaList);
        recyclerMedia.setAdapter(mediaAdapter);

        // Firebase reference
        mediaRef = FirebaseDatabase.getInstance().getReference("Media");

        // Set up spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mediaTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = mediaTypes[position];
                loadMediaByType(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadMediaByType(String type) {
        mediaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mediaList.clear();
                for (DataSnapshot mediaSnap : snapshot.getChildren()) {
                    Media media = mediaSnap.getValue(Media.class);
                    if (media != null && media.getType() != null) {
                        String typeFormatted = media.getType().toLowerCase().replace(" ", "").replace("_", "");
                        String selectedFormatted = type.toLowerCase().replace(" ", "").replace("_", "");
                        if (typeFormatted.contains(selectedFormatted)) {
                            mediaList.add(media);
                        }
                    }
                }
                mediaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase error (optional)
            }
        });
    }
}












