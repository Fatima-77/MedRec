package com.example.medrec;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medrec.adapter.LibraryAdapter;
import com.example.medrec.adapter.TrendingMediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class LibraryActivity extends BaseActivity {

    private Spinner spinnerSort;
    private ImageView iconFilter;
    private LinearLayout filterPanel, genreCheckboxContainer;
    private EditText editSearch;
    private Button btnApplyFilters;
    private RecyclerView recyclerLibrary, recyclerRecommendations;
    private LibraryAdapter libraryAdapter;
    private TrendingMediaAdapter recAdapter;
    private List<Media> userLibraryList = new ArrayList<>();
    private List<Media> filteredLibraryList = new ArrayList<>();
    private List<Media> recommendationList = new ArrayList<>();
    private Map<String, Integer> genreTapStates = new HashMap<>(); // 0=neutral, 1=include, 2=exclude
    private final String[] sortOptions = {"Alphabetical", "Date Added", "User Rating", "Status"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        setPageTitle("Library");

        iconFilter = findViewById(R.id.iconFilter);
        filterPanel = findViewById(R.id.filterPanel);
        genreCheckboxContainer = findViewById(R.id.genreCheckboxContainer);
        editSearch = findViewById(R.id.editSearch);
        spinnerSort = findViewById(R.id.spinnerSort);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        recyclerLibrary = findViewById(R.id.recyclerLibrary);
        recyclerRecommendations = findViewById(R.id.recyclerRecommendations);

        spinnerSort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortOptions));

        libraryAdapter = new LibraryAdapter(this, filteredLibraryList, media -> {
            // On click: open MediaDetailActivity
            // Pass media ID or object as needed
        });
        recyclerLibrary.setLayoutManager(new LinearLayoutManager(this));
        recyclerLibrary.setAdapter(libraryAdapter);

        recAdapter = new TrendingMediaAdapter(this, recommendationList, media -> {
            // Placeholder click
        });
        recyclerRecommendations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecommendations.setAdapter(recAdapter);

        // Filter icon logic
        iconFilter.setOnClickListener(v -> {
            if (filterPanel.getVisibility() == View.GONE) {
                filterPanel.setVisibility(View.VISIBLE);
            } else {
                filterPanel.setVisibility(View.GONE);
            }
        });

        btnApplyFilters.setOnClickListener(v -> {
            applyFilters();
            filterPanel.setVisibility(View.GONE);
        });

        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                applyFilters();
                filterPanel.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { applyFilters(); }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            public void afterTextChanged(Editable s) {}
        });

        fetchUserLibraryAndGenres();
        fetchRecommendations(); // Placeholder for now
    }

    private void fetchUserLibraryAndGenres() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userLibRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Library");
        DatabaseReference mediaRef = FirebaseDatabase.getInstance().getReference("Media");

        userLibRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot userLibSnap) {
                userLibraryList.clear();
                Set<String> genreSet = new HashSet<>();
                for (DataSnapshot entrySnap : userLibSnap.getChildren()) {
                    String mediaId = entrySnap.getKey();
                    String status = entrySnap.child("status").getValue(String.class);
                    Double userRating = entrySnap.child("rating").getValue(Double.class);
                    Long timestamp = entrySnap.child("timestamp").getValue(Long.class);

                    mediaRef.child(mediaId).addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot mediaSnap) {
                            Media media = mediaSnap.getValue(Media.class);
                            if (media != null) {
                                media.setStatus(status);
                                media.setUserRating(userRating);
                                media.setTimestamp(timestamp);

                                userLibraryList.add(media);

                                List<String> categories = media.getCategory() != null ? media.getCategory() : new ArrayList<>();
                                genreSet.addAll(categories);

                                List<String> allGenres = new ArrayList<>(genreSet);
                                Collections.sort(allGenres);
                                setupGenreCheckboxes(allGenres);
                                applyFilters();
                            }
                        }
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupGenreCheckboxes(List<String> allGenres) {
        genreCheckboxContainer.removeAllViews();
        genreTapStates.clear();
        for (String genre : allGenres) {
            CheckBox cb = new CheckBox(this);
            cb.setText(genre);
            cb.setButtonDrawable(R.drawable.ic_checkbox_unchecked);
            genreTapStates.put(genre, 0);

            cb.setOnClickListener(v -> {
                int state = genreTapStates.get(genre);
                state = (state + 1) % 3;
                genreTapStates.put(genre, state);

                if (state == 0) {
                    cb.setText(genre);
                    cb.setButtonDrawable(R.drawable.ic_checkbox_unchecked);
                } else if (state == 1) {
                    cb.setText(genre + " (Include)");
                    cb.setButtonDrawable(R.drawable.ic_checkbox_checked);
                } else if (state == 2) {
                    cb.setText(genre + " (Exclude)");
                    cb.setButtonDrawable(R.drawable.ic_checkbox_exclude);
                }
            });
            genreCheckboxContainer.addView(cb);
        }
    }

    private void applyFilters() {
        String searchQuery = editSearch.getText().toString().toLowerCase();
        int sortIdx = spinnerSort.getSelectedItemPosition();

        filteredLibraryList.clear();

        for (Media media : userLibraryList) {
            // Search filter
            if (!media.getTitle().toLowerCase().contains(searchQuery)) continue;

            // Genre filter (Include/Exclude logic)
            boolean excluded = false;
            boolean included = false;
            List<String> categories = media.getCategory() != null ? media.getCategory() : new ArrayList<>();
            for (String genre : genreTapStates.keySet()) {
                int state = genreTapStates.get(genre);
                if (state == 1 && !categories.contains(genre)) { included = true; }
                if (state == 2 && categories.contains(genre)) { excluded = true; break; }
            }
            if (excluded) continue;
            if (anyIncludeGenre() && !matchesInclude(media)) continue;

            filteredLibraryList.add(media);
        }

        // Sorting
        Comparator<Media> comparator = null;
        switch (sortIdx) {
            case 0: // Alphabetical
                comparator = Comparator.comparing(Media::getTitle, String::compareToIgnoreCase);
                break;
            case 1: // Date Added (descending)
                comparator = (a, b) -> Long.compare(b.getTimestamp() != null ? b.getTimestamp() : 0, a.getTimestamp() != null ? a.getTimestamp() : 0);
                break;
            case 2: // User Rating
                comparator = Comparator.comparingDouble((Media m) -> m.getUserRating() != null ? m.getUserRating() : 0).reversed();
                break;
            case 3: // Status (Completed first)
                comparator = (a, b) -> {
                    String sA = a.getStatus() != null ? a.getStatus() : "";
                    String sB = b.getStatus() != null ? b.getStatus() : "";
                    // Prioritize Completed, then Watching, then Plan, then Dropped
                    List<String> order = Arrays.asList("Completed", "Watching", "Plan to Watch", "Dropped");
                    int idxA = order.indexOf(sA);
                    int idxB = order.indexOf(sB);
                    return Integer.compare(idxA < 0 ? 99 : idxA, idxB < 0 ? 99 : idxB);
                };
                break;
        }
        if (comparator != null) Collections.sort(filteredLibraryList, comparator);

        libraryAdapter.notifyDataSetChanged();
    }

    private boolean anyIncludeGenre() {
        for (int state : genreTapStates.values()) if (state == 1) return true;
        return false;
    }
    private boolean matchesInclude(Media media) {
        if (media.getCategory() == null) return false;
        for (String genre : genreTapStates.keySet()) {
            int state = genreTapStates.get(genre);
            if (state == 1 && media.getCategory().contains(genre)) return true;
        }
        return false;
    }

    // Placeholder: show trending media as recommendations (you'll later replace with real ML results)
    private void fetchRecommendations() {
        DatabaseReference mediaRef = FirebaseDatabase.getInstance().getReference("Media");
        mediaRef.orderByChild("rating").limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recommendationList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Media media = snap.getValue(Media.class);
                            if (media != null) recommendationList.add(0, media);
                        }
                        recAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}


