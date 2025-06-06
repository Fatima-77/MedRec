package com.example.medrec.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medrec.R;
import com.example.medrec.model.Media;
import com.example.medrec.MediaDetailActivity;

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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MediaDetailActivity.class);
            intent.putExtra("media_id", media.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, typeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.mediaTitle);
            typeText = itemView.findViewById(R.id.mediaType);
        }
    }
}
