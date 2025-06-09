package com.example.medrec.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.medrec.MediaDetailActivity;
import com.example.medrec.R;
import com.example.medrec.model.Media;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private Context context;
    private List<Media> mediaList;

    public MediaAdapter(Context context, List<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media media = mediaList.get(position);
        holder.titleText.setText(media.getTitle());
        holder.typeText.setText(media.getType());

        // Use getCover() to load the image from Firebase
        Glide.with(context)
                .load(media.getCover())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.coverImage);

        // Send the full Media object to the detail screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaDetailActivity.class);
            intent.putExtra("Media", media);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, typeText;
        ImageView coverImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.mediaTitle);
            typeText = itemView.findViewById(R.id.mediaType);
            coverImage = itemView.findViewById(R.id.mediaCover);
        }
    }
}

