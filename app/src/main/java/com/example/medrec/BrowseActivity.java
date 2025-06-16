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
import com.example.medrec.adapter.MediaAdapter;
import com.example.medrec.model.Media;
import com.google.firebase.database.*;
import java.util.*;

public class BrowseActivity extends BaseActivity {

    private Spinner spinnerType, spinnerSort;
    private ImageView iconFilter;
    private LinearLayout filterPanel, genreCheckboxContainer;
    private EditText editSearch;
    private Button btnApplyFilters;
    private RecyclerView recyclerMedia;
    private MediaAdapter mediaAdapter;
    private List<Media> allMediaList = new ArrayList<>();
    private List<Media> filteredMediaList = new ArrayList<>();
    private Map<String, Integer> genreTapStates = new HashMap<>(); // 0=neutral, 1=include, 2=exclude

    private final String[] mediaTypes = {"Anime", "Manga/Manhwa", "Light Novel", "Book"};
    private final String[] sortOptions = {"Alphabetical (A–Z)", "Alphabetical (Z–A)", "Popularity", "Rating"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        setPageTitle("Browse");

        spinnerType = findViewById(R.id.spinnerType);
        iconFilter = findViewById(R.id.iconFilter);
        filterPanel = findViewById(R.id.filterPanel);
        genreCheckboxContainer = findViewById(R.id.genreCheckboxContainer);
        editSearch = findViewById(R.id.editSearch);
        spinnerSort = findViewById(R.id.spinnerSort);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        recyclerMedia = findViewById(R.id.recyclerMedia);

        // Setup adapters
        spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mediaTypes));
        spinnerSort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortOptions));

        // Setup RecyclerView
        mediaAdapter = new MediaAdapter(this, filteredMediaList);
        recyclerMedia.setLayoutManager(new LinearLayoutManager(this));
        recyclerMedia.setAdapter(mediaAdapter);

        // Filter icon logic (expand/collapse)
        iconFilter.setOnClickListener(v -> {
            if (filterPanel.getVisibility() == View.GONE) {
                filterPanel.setVisibility(View.VISIBLE);
            } else {
                filterPanel.setVisibility(View.GONE);
            }
        });

        // Collapse panel on Apply
        btnApplyFilters.setOnClickListener(v -> {
            applyFilters();
            filterPanel.setVisibility(View.GONE);
        });

        // Collapse panel on pressing Enter in search
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                applyFilters();
                filterPanel.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        // Filter + search + sort listeners (instant feedback)
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { applyFilters(); }
            public void onNothingSelected(AdapterView<?> parent) {}
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

        // Fetch all media and genres from Firebase
        fetchAllMediaAndGenres();
    }

    private void fetchAllMediaAndGenres() {
        DatabaseReference mediaRef = FirebaseDatabase.getInstance().getReference("Media");
        mediaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMediaList.clear();
                Set<String> genreSet = new HashSet<>();
                for (DataSnapshot mediaSnap : snapshot.getChildren()) {
                    Media media = mediaSnap.getValue(Media.class);
                    if (media != null) {
                        allMediaList.add(media);
                        // Dynamically collect genres from category field
                        List<String> categories = null;
                        Object catsObj = mediaSnap.child("category").getValue();
                        if (catsObj instanceof List) {
                            categories = (List<String>) catsObj;
                        } else if (catsObj instanceof String) {
                            categories = Arrays.asList(((String) catsObj).split(","));
                        }
                        if (categories != null) {
                            for (String genre : categories) {
                                if (genre != null && !genre.trim().isEmpty()) {
                                    genreSet.add(genre.trim());
                                }
                            }
                        }
                    }
                }
                List<String> allGenres = new ArrayList<>(genreSet);
                Collections.sort(allGenres);
                setupGenreCheckboxes(allGenres); // Build genre checkboxes dynamically
                applyFilters();
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
            cb.setButtonDrawable(R.drawable.ic_checkbox_unchecked); // default
            genreTapStates.put(genre, 0);

            cb.setOnClickListener(v -> {
                int state = genreTapStates.get(genre);
                state = (state + 1) % 3; // 0: neutral, 1: include, 2: exclude
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
        String selectedType = spinnerType.getSelectedItem().toString();
        String searchQuery = editSearch.getText().toString().toLowerCase();
        int sortIdx = spinnerSort.getSelectedItemPosition();

        filteredMediaList.clear();

        for (Media media : allMediaList) {
            // Type filter
            if (!media.getType().equalsIgnoreCase(selectedType)) continue;
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

            filteredMediaList.add(media);
        }

        // Sorting
        Comparator<Media> comparator = null;
        switch (sortIdx) {
            case 0: // A–Z
                comparator = Comparator.comparing(Media::getTitle, String::compareToIgnoreCase);
                break;
            case 1: // Z–A
                comparator = (a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle());
                break;
            case 2: // Popularity
                comparator = Comparator.comparingDouble((Media m) -> m.getRating() != null ? m.getRating() : 0).reversed();
                break;
            case 3: // Rating
                comparator = Comparator.comparingDouble((Media m) -> m.getRating() != null ? m.getRating() : 0).reversed();
                break;
        }
        if (comparator != null) Collections.sort(filteredMediaList, comparator);

        mediaAdapter.notifyDataSetChanged();
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
}















