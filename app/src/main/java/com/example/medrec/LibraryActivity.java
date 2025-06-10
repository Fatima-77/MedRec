package com.example.medrec;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medrec.adapter.MediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private final List<Media> userLibraryList = new ArrayList<>();
    private final DatabaseReference mediaRef = FirebaseDatabase.getInstance().getReference("Media");
    private final DatabaseReference userRef = FirebaseDatabase.getInstance()
            .getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("interactions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        setPageTitle("My Library");

        recyclerView = findViewById(R.id.recyclerLibrary);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mediaAdapter = new MediaAdapter(this, userLibraryList);
        recyclerView.setAdapter(mediaAdapter);

        loadUserLibrary();
    }

    private void loadUserLibrary() {
        userLibraryList.clear();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(LibraryActivity.this, "No saved media yet", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> mediaIds = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    mediaIds.add(item.getKey());
                }

                mediaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot allMediaSnap) {
                        for (DataSnapshot item : allMediaSnap.getChildren()) {
                            Media media = item.getValue(Media.class);
                            if (media != null && mediaIds.contains(media.getId())) {
                                userLibraryList.add(media);
                            }
                        }
                        mediaAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LibraryActivity.this, "Media fetch error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LibraryActivity.this, "Error loading library", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

