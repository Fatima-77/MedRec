package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.KeyEvent;
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

    private Spinner spinnerType, spinnerSort;
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
    private Map<String,Integer> genreTapStates = new HashMap<>();

    private final String[] mediaTypes = {"All","Anime","Manga/Manhwa","Light Novel","Book"};
    private final String[] sortOptions  = {"Alphabetical","Date Added","User Rating","Status"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        setPageTitle("Library");

        // Find views
        spinnerType             = findViewById(R.id.spinnerType);
        spinnerSort             = findViewById(R.id.spinnerSort);
        iconFilter              = findViewById(R.id.iconFilter);
        filterPanel             = findViewById(R.id.filterPanel);
        genreCheckboxContainer  = findViewById(R.id.genreCheckboxContainer);
        editSearch              = findViewById(R.id.editSearch);
        btnApplyFilters         = findViewById(R.id.btnApplyFilters);
        recyclerLibrary         = findViewById(R.id.recyclerLibrary);
        recyclerRecommendations = findViewById(R.id.recyclerRecommendations);

        // Setup spinners
        spinnerType.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mediaTypes));
        spinnerType.setSelection(0);
        spinnerSort.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sortOptions));

        // Library RecyclerView
        libraryAdapter = new LibraryAdapter(this, filteredLibraryList, media -> {
            Intent i = new Intent(this, MediaDetailActivity.class);
            i.putExtra("Media", media);
            startActivity(i);
        });
        recyclerLibrary.setLayoutManager(new LinearLayoutManager(this));
        recyclerLibrary.setAdapter(libraryAdapter);

        // Recommendations RecyclerView
        recAdapter = new TrendingMediaAdapter(this, recommendationList, media -> {
            Intent i = new Intent(this, MediaDetailActivity.class);
            i.putExtra("Media", media);
            startActivity(i);
        });
        recyclerRecommendations.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecommendations.setAdapter(recAdapter);

        // Filter panel toggle
        iconFilter.setOnClickListener(v ->
                filterPanel.setVisibility(filterPanel.getVisibility()==View.GONE
                        ? View.VISIBLE : View.GONE));

        // Apply button
        btnApplyFilters.setOnClickListener(v -> {
            applyFilters();
            filterPanel.setVisibility(View.GONE);
        });

        // Enter on search collapses + applies
        editSearch.setOnEditorActionListener((v, actionId, ev) -> {
            if (actionId==EditorInfo.IME_ACTION_DONE ||
                    (ev!=null && ev.getKeyCode()==KeyEvent.KEYCODE_ENTER && ev.getAction()==KeyEvent.ACTION_DOWN)) {
                applyFilters();
                filterPanel.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        // Live filter triggers
        spinnerType.setOnItemSelectedListener(new SimpleItemListener());
        spinnerSort.setOnItemSelectedListener(new SimpleItemListener());
        editSearch.addTextChangedListener(new SimpleTextWatcher());

        // Load user library & genres + recommendations
        fetchUserLibraryAndGenres();
        fetchRecommendations();
    }

    private class SimpleItemListener implements AdapterView.OnItemSelectedListener {
        @Override public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
            applyFilters();
        }
        @Override public void onNothingSelected(AdapterView<?> p) {}
    }
    private class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
        @Override public void onTextChanged(CharSequence s,int a,int b,int c){ applyFilters(); }
        @Override public void afterTextChanged(Editable e){}
    }

    private void fetchUserLibraryAndGenres() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference libRef   = FirebaseDatabase.getInstance()
                .getReference("Users").child(uid).child("Library");
        DatabaseReference mediaRef = FirebaseDatabase.getInstance()
                .getReference("Media");

        libRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                userLibraryList.clear();
                Set<String> genreSet = new HashSet<>();
                List<String> mediaIds = new ArrayList<>();
                Map<String,DataSnapshot> entries = new HashMap<>();

                for (DataSnapshot e : snap.getChildren()) {
                    mediaIds.add(e.getKey());
                    entries.put(e.getKey(), e);
                }
                if (mediaIds.isEmpty()) {
                    setupGenreCheckboxes(new ArrayList<>());
                    applyFilters();
                    return;
                }
                final int total = mediaIds.size(), count[] = {0};
                for (String mid : mediaIds) {
                    DataSnapshot entrySnap = entries.get(mid);
                    String status         = entrySnap.child("status").getValue(String.class);
                    Double ur             = entrySnap.child("rating").getValue(Double.class);
                    Long ts               = entrySnap.child("timestamp").getValue(Long.class);

                    mediaRef.child(mid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override public void onDataChange(@NonNull DataSnapshot mSnap) {
                                    Media m = mSnap.getValue(Media.class);
                                    if (m!=null) {
                                        m.setStatus(status);
                                        m.setUserRating(ur);
                                        m.setTimestamp(ts);
                                        userLibraryList.add(m);
                                        if (m.getCategory()!=null) genreSet.addAll(m.getCategory());
                                    }
                                    if (++count[0]==total) {
                                        List<String> allGenres = new ArrayList<>(genreSet);
                                        Collections.sort(allGenres);
                                        setupGenreCheckboxes(allGenres);
                                        applyFilters();
                                    }
                                }
                                @Override public void onCancelled(@NonNull DatabaseError e) {
                                    if (++count[0]==total) {
                                        List<String> allGenres = new ArrayList<>(genreSet);
                                        Collections.sort(allGenres);
                                        setupGenreCheckboxes(allGenres);
                                        applyFilters();
                                    }
                                }
                            });
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e){}
        });
    }

    private void setupGenreCheckboxes(List<String> allGenres) {
        genreCheckboxContainer.removeAllViews();
        genreTapStates.clear();
        for (String g : allGenres) {
            final String genre = g;
            CheckBox cb = new CheckBox(this);
            cb.setText(genre);
            cb.setButtonDrawable(R.drawable.ic_checkbox_unchecked);
            genreTapStates.put(genre,0);
            cb.setOnClickListener(v -> {
                int s = (genreTapStates.get(genre)+1)%3;
                genreTapStates.put(genre,s);
                switch(s){
                    case 0: cb.setText(genre); cb.setButtonDrawable(R.drawable.ic_checkbox_unchecked); break;
                    case 1: cb.setText(genre+" (Include)"); cb.setButtonDrawable(R.drawable.ic_checkbox_checked); break;
                    case 2: cb.setText(genre+" (Exclude)"); cb.setButtonDrawable(R.drawable.ic_checkbox_exclude); break;
                }
            });
            genreCheckboxContainer.addView(cb);
        }
    }

    private void applyFilters() {
        String type = spinnerType.getSelectedItem().toString();
        String q    = editSearch.getText().toString().toLowerCase();
        int sortIdx = spinnerSort.getSelectedItemPosition();
        filteredLibraryList.clear();

        for (Media m : userLibraryList) {
            if (!"All".equals(type) && !m.getType().equalsIgnoreCase(type)) continue;
            if (!m.getTitle().toLowerCase().contains(q)) continue;

            boolean exclude=false, include=false;
            List<String> cats = m.getCategory()==null
                    ? Collections.emptyList() : m.getCategory();
            for (String genre : genreTapStates.keySet()) {
                int st = genreTapStates.get(genre);
                if (st==1 && !cats.contains(genre)) include=true;
                if (st==2 &&  cats.contains(genre)) { exclude=true; break; }
            }
            if (exclude) continue;
            if (genreTapStates.containsValue(1) && !matchesInclude(m)) continue;
            filteredLibraryList.add(m);
        }

        Comparator<Media> cmp = null;
        switch(sortIdx) {
            case 0: cmp = Comparator.comparing(Media::getTitle, String::compareToIgnoreCase); break;
            case 1: cmp = (a,b)->Long.compare(
                    b.getTimestamp()==null?0:b.getTimestamp(),
                    a.getTimestamp()==null?0:a.getTimestamp()); break;
            case 2: cmp = Comparator.comparingDouble(
                    (Media mm)->mm.getUserRating()==null?0:mm.getUserRating()).reversed(); break;
            case 3:
                List<String> ord=Arrays.asList("Completed","Watching","Plan to Watch","Dropped");
                cmp=(a,b)->Integer.compare(
                        ord.indexOf(a.getStatus())<0?99:ord.indexOf(a.getStatus()),
                        ord.indexOf(b.getStatus())<0?99:ord.indexOf(b.getStatus()));
                break;
        }
        if (cmp!=null) Collections.sort(filteredLibraryList, cmp);
        libraryAdapter.notifyDataSetChanged();
    }

    private boolean matchesInclude(Media m) {
        if (m.getCategory()==null) return false;
        for (String ke : genreTapStates.keySet()) {
            if (genreTapStates.get(ke)==1 &&
                    m.getCategory().contains(ke)) return true;
        }
        return false;
    }

    private void fetchRecommendations() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Media");
        ref.orderByChild("rating").limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        recommendationList.clear();
                        for (DataSnapshot s: snap.getChildren()) {
                            Media m = s.getValue(Media.class);
                            if (m!=null) recommendationList.add(0,m);
                        }
                        recAdapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e){}
                });
    }
}





