package com.herdaynote.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    Context context;
    ArrayList<Music> music;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView musicTitle, musicArtist;
        ImageView musicCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            musicTitle = itemView.findViewById(R.id.musicTitle);
            musicArtist = itemView.findViewById(R.id.musicArtist);
            musicCover = itemView.findViewById(R.id.musicCover);
        }
    }

    public MusicAdapter(Context context, ArrayList<Music> music) {
        this.context = context;
        this.music = music;
    }

    @NonNull
    @NotNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.music_item, parent, false
        );

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MusicAdapter.MyViewHolder holder, int position) {
        holder.musicTitle.setText(music.get(position).getTitle());
        holder.musicTitle.setSelected(true);
        holder.musicArtist.setText(music.get(position).getArtist());
        holder.musicArtist.setSelected(true);

        if (music.get(position).getCover() != null) {
            holder.musicCover.setImageBitmap(music.get(position).getCover());
        } else {
            holder.musicCover.setImageResource(R.drawable.music_cover);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent openMusicPlayer = new Intent(context, MusicPlayer.class);
            openMusicPlayer.putExtra("position", position);
            context.startActivity(openMusicPlayer);
        });
    }

    @Override
    public int getItemCount() {
        return music.size();
    }
}