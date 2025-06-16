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

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private final List<Media> libraryList;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Media media);
    }

    public LibraryAdapter(Context context, List<Media> libraryList, OnItemClickListener listener) {
        this.context = context;
        this.libraryList = libraryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_library_media, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Media media = libraryList.get(position);
        holder.textTitle.setText(media.getTitle());
        holder.textStatus.setText(media.getStatus());
        holder.textUserRating.setText(media.getUserRating() != null ? "Your rating: " + media.getUserRating() : "");
        Glide.with(context)
                .load(media.getCover())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(media));
    }

    @Override
    public int getItemCount() {
        return libraryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView textTitle, textStatus, textUserRating;

        public ViewHolder(View v) {
            super(v);
            imgCover = v.findViewById(R.id.imgCover);
            textTitle = v.findViewById(R.id.textTitle);
            textStatus = v.findViewById(R.id.textStatus);
            textUserRating = v.findViewById(R.id.textUserRating);
        }
    }
}

