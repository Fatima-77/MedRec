package com.example.medrec;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medrec.adapter.TrendingMediaAdapter;
import com.example.medrec.model.Media;
import androidx.annotation.NonNull;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView titleTyping;
    private RecyclerView carouselRecycler;
    private TrendingMediaAdapter carouselAdapter;
    private List<Media> trendingList = new ArrayList<>();

    public static int getStatusBarHeight(android.content.Context c) {
        int resId = c.getResources().getIdentifier("status_bar_height","dimen","android");
        return resId>0 ? c.getResources().getDimensionPixelSize(resId) : 0;
    }

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        titleTyping = findViewById(R.id.text_typing_animation);
        Button btnBrowse = findViewById(R.id.btn_browse);
        Button btnLibrary = findViewById(R.id.btn_library);
        ImageView iconProfile = findViewById(R.id.icon_profile);
        ImageView iconSettings = findViewById(R.id.icon_settings);
        LinearLayout banner = findViewById(R.id.main_banner);

        // status bar padding
        if (banner!=null) {
            int h = getStatusBarHeight(this);
            banner.setPadding(banner.getPaddingLeft(),h,
                    banner.getPaddingRight(),banner.getPaddingBottom());
        }

        // animate
        startTypingAnimation("Welcome to MediaNest");

        // nav
        btnBrowse.setOnClickListener(v->startActivity(
                new Intent(this, BrowseActivity.class)));
        btnLibrary.setOnClickListener(v->startActivity(
                new Intent(this, LibraryActivity.class)));
        iconProfile.setOnClickListener(v->startActivity(
                new Intent(this, ProfileActivity.class)));
        iconSettings.setOnClickListener(v->startActivity(
                new Intent(this, SettingsActivity.class)));

        // carousel RecyclerView
        carouselRecycler = findViewById(R.id.viewpager_carousel);
        carouselAdapter = new TrendingMediaAdapter(this, trendingList, m->{
            Intent i=new Intent(this,MediaDetailActivity.class);
            i.putExtra("Media",m);
            startActivity(i);
        });
        carouselRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        carouselRecycler.setAdapter(carouselAdapter);

        // fetch
        fetchTrendingMedia();
    }

    private void startTypingAnimation(String text) {
        Handler h=new Handler();
        final int[] i={0};
        Runnable r=new Runnable(){
            public void run(){
                if(i[0]<=text.length()){
                    titleTyping.setText(text.substring(0,i[0]++));
                    h.postDelayed(this,45);
                }
            }
        };
        h.post(r);
    }

    private void fetchTrendingMedia(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Media");
        ref.orderByChild("rating").limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    public void onDataChange(@NonNull DataSnapshot snap){
                        trendingList.clear();
                        for(DataSnapshot s: snap.getChildren()){
                            Media m=s.getValue(Media.class);
                            if(m!=null) trendingList.add(m);
                        }
                        Collections.reverse(trendingList);
                        carouselAdapter.notifyDataSetChanged();
                    }
                    public void onCancelled(@NonNull DatabaseError e){}
                });
    }
}






