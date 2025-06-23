package com.example.medrec;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medrec.adapter.MediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendationsActivity extends BaseActivity {

    private Spinner spinnerType, spinnerSort;
    private ImageView iconFilter;
    private LinearLayout filterPanel, genreCheckboxContainer;
    private EditText editSearch;
    private Button btnApplyFilters;
    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private List<Media> fullRecList     = new ArrayList<>();
    private List<Media> filteredList    = new ArrayList<>();
    private Map<String,Integer> genreTapStates = new HashMap<>();

    private FirebaseAuth auth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);
        setPageTitle("Recommendations");

        auth    = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        // ① Bind views
        spinnerType            = findViewById(R.id.spinnerType);
        spinnerSort            = findViewById(R.id.spinnerSort);
        iconFilter             = findViewById(R.id.iconFilter);
        filterPanel            = findViewById(R.id.filterPanel);
        genreCheckboxContainer = findViewById(R.id.genreCheckboxContainer);
        editSearch             = findViewById(R.id.editSearch);
        btnApplyFilters        = findViewById(R.id.btnApplyFilters);
        recyclerView           = findViewById(R.id.recommendationsRecyclerView);

        // ② Populate your sort options
        String[] sortOptions = {
                "Alphabetical (A–Z)",
                "Alphabetical (Z–A)",
                "Popularity",
                "Rating"
        };
        spinnerSort.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        sortOptions)
        );

        // ③ Filter icon toggles the filter panel
        iconFilter.setOnClickListener(v -> {
            filterPanel.setVisibility(
                    filterPanel.getVisibility() == View.VISIBLE
                            ? View.GONE
                            : View.VISIBLE
            );
        });

        // ④ Apply button
        btnApplyFilters.setOnClickListener(v -> {
            applyFilters();
            filterPanel.setVisibility(View.GONE);
        });

        // ⑤ RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MediaAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        // ⑥ Fetch recommendations & seed filters/genres
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        String uid = auth.getCurrentUser().getUid();
        rootRef.child("Recommendations").child(uid)
                .get().addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(this,
                                "No recommendations found.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    fullRecList.clear();
                    // Gather all children, then once done, build genres & filter
                    // Because these callbacks fire per media item, we'll
                    // rebuild filters incrementally as items arrive.
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String mediaId = child.getKey();
                        rootRef.child("Media").child(mediaId)
                                .get().addOnSuccessListener(mediaSnap -> {
                                    Media m = mediaSnap.getValue(Media.class);
                                    if (m != null) {
                                        fullRecList.add(m);

                                        // On first load, build genre checkboxes:
                                        if (genreTapStates.isEmpty()) {
                                            Set<String> genres = new HashSet<>();
                                            for (Media mm : fullRecList) {
                                                if (mm.getCategory() != null) {
                                                    genres.addAll(mm.getCategory());
                                                }
                                            }
                                            List<String> allGenres = new ArrayList<>(genres);
                                            Collections.sort(allGenres);
                                            setupGenreCheckboxes(allGenres);
                                        }

                                        // Refresh filtered view:
                                        applyFilters();
                                    }
                                });
                    }
                });
    }

    private void setupGenreCheckboxes(List<String> allGenres) {
        genreCheckboxContainer.removeAllViews();
        genreTapStates.clear();

        for (String genre : allGenres) {
            CheckBox cb = new CheckBox(this);
            cb.setText(genre);
            genreTapStates.put(genre, 0);

            cb.setOnClickListener(v -> {
                int state = (genreTapStates.get(genre) + 1) % 3;
                genreTapStates.put(genre, state);
                if (state == 0) {
                    cb.setText(genre);
                } else if (state == 1) {
                    cb.setText(genre + " (Include)");
                } else {
                    cb.setText(genre + " (Exclude)");
                }
            });

            genreCheckboxContainer.addView(cb);
        }
    }

    private void applyFilters() {
        String selectedType = spinnerType.getSelectedItem() == null
                ? "All"
                : spinnerType.getSelectedItem().toString();
        String searchQuery = editSearch.getText().toString()
                .trim().toLowerCase();
        int sortIdx = spinnerSort.getSelectedItemPosition();

        filteredList.clear();
        for (Media m : fullRecList) {
            // Type filter
            if (!selectedType.equals("All") &&
                    !m.getType().equalsIgnoreCase(selectedType)) {
                continue;
            }
            // Search filter
            if (!m.getTitle().toLowerCase()
                    .contains(searchQuery)) {
                continue;
            }
            // Genre include/exclude
            boolean excluded = false;
            boolean includesRequired = false;
            List<String> cats = m.getCategory() != null
                    ? m.getCategory()
                    : new ArrayList<>();
            for (Map.Entry<String,Integer> e : genreTapStates.entrySet()) {
                String g = e.getKey();
                int state = e.getValue();
                if (state == 2 && cats.contains(g)) {
                    excluded = true;
                    break;
                }
                if (state == 1) {
                    includesRequired = true;
                    if (!cats.contains(g)) {
                        excluded = true;
                        break;
                    }
                }
            }
            if (excluded) continue;
            if (includesRequired && !matchesInclude(m)) continue;

            filteredList.add(m);
        }

        // Sorting
        Comparator<Media> comp = null;
        switch (sortIdx) {
            case 0:
                comp = Comparator.comparing(
                        Media::getTitle,
                        String::compareToIgnoreCase
                );
                break;
            case 1:
                comp = (a,b) ->
                        b.getTitle().compareToIgnoreCase(a.getTitle());
                break;
            case 2: // popularity
            case 3: // rating
                comp = Comparator.comparingDouble(
                        (Media mm) -> mm.getRating() != null
                                ? mm.getRating() : 0
                ).reversed();
                break;
        }
        if (comp != null) {
            Collections.sort(filteredList, comp);
        }

        adapter.notifyDataSetChanged();
    }

    private boolean matchesInclude(Media m) {
        if (m.getCategory() == null) return false;
        for (Map.Entry<String,Integer> e : genreTapStates.entrySet()) {
            if (e.getValue() == 1 &&
                    m.getCategory().contains(e.getKey())) {
                return true;
            }
        }
        return false;
    }
}
