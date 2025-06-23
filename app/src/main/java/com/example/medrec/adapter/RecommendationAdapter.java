package com.example.medrec.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.medrec.R;
import com.example.medrec.model.Media;
import java.util.List;

public class RecommendationAdapter
        extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private final List<Media> mediaList;
    private final Context context;

    public RecommendationAdapter(List<Media> mediaList, Context context) {
        this.mediaList = mediaList;
        this.context   = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {
        Media media = mediaList.get(position);
        holder.tvTitle.setText(media.getTitle());
        Glide.with(context)
                .load(media.getCover())
                .into(holder.imgMedia);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMedia;
        TextView  tvTitle;
        ViewHolder(View itemView) {
            super(itemView);
            imgMedia = itemView.findViewById(R.id.imgMedia);
            tvTitle  = itemView.findViewById(R.id.tvTitle);
        }
    }
}
