package com.example.medrec.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.medrec.R;
import com.example.medrec.model.Media;
import android.widget.TextView;

import java.util.List;

public class TrendingMediaAdapter extends RecyclerView.Adapter<TrendingMediaAdapter.ViewHolder> {
    private final List<Media> trendingList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Media media);
    }

    public TrendingMediaAdapter(Context context, List<Media> trendingList, OnItemClickListener listener) {
        this.context = context;
        this.trendingList = trendingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trending_media, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media media = trendingList.get(position);
        Glide.with(context)
                .load(media.getCover())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCover);
        holder.textTitle.setText(media.getTitle());
        holder.imgCover.setOnClickListener(v -> listener.onItemClick(media));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView textTitle;
        public ViewHolder(View v) {
            super(v);
            imgCover = v.findViewById(R.id.imgTrendingCover);
            textTitle = v.findViewById(R.id.textTrendingTitle);
        }
    }
}
