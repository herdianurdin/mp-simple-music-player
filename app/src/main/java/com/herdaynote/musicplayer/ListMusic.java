package com.herdaynote.musicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ListMusic extends Activity {
    static ArrayList<Music> musicArrayList;
    RecyclerView recyclerView;
    MusicAdapter adapter;
    static Boolean shuffle = false, repeat = true;
    static Boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_music);

        recyclerView = findViewById(R.id.listMusic);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        musicArrayList = new ArrayList<>();

        getMusic();
        adapter = new MusicAdapter(this, musicArrayList);

        (new Handler()).postDelayed(() -> recyclerView.setAdapter(adapter), 600);
    }

    private void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };

        @SuppressLint("Recycle") Cursor musicCursor = contentResolver.query(
                musicUri, projection, null, null, null
        );
        if (musicCursor != null) {
            while (musicCursor.moveToNext()) {
                String musicTitle = musicCursor.getString(0);
                String musicArtist = musicCursor.getString(1);
                String musicPath = musicCursor.getString(2);
                Bitmap musicCover = convertCover(musicPath);

                musicArrayList.add(new Music(musicTitle, musicArtist, musicPath, musicCover));
            }
        }
    }

    public static Bitmap convertCover(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] cover = retriever.getEmbeddedPicture();
        retriever.release();

        if (cover == null) {
            return null;
        }

        return BitmapFactory.decodeByteArray(cover, 0, cover.length);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}