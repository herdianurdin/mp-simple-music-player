package com.herdaynote.musicplayer;

import static com.herdaynote.musicplayer.ListMusic.convertCover;
import static com.herdaynote.musicplayer.ListMusic.isPlaying;
import static com.herdaynote.musicplayer.ListMusic.musicArrayList;
import static com.herdaynote.musicplayer.ListMusic.repeat;
import static com.herdaynote.musicplayer.ListMusic.shuffle;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.herdaynote.musicplayer.Services.OnClearFromRecentService;

import java.util.Random;

public class MusicPlayer extends Activity implements Playable {
    TextView musicTitle, musicArtist, durationPlay, durationTotal;
    ImageView btnPrev, btnPlay, btnNext, btnShuffle, btnRepeat, musicCover;
    SeekBar seekBarTime;
    static MediaPlayer mediaPlayer;
    int position;
    Uri uri;
    static NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);
        initViews();
        getIntentMethod();

        isPlaying = true;

        createChannel();
        registerReceiver(broadcastReceiver, new IntentFilter("TRACK_TRACK"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        CreateNotification.createNotification(this, position, R.drawable.pause_button);

        btnPrevMethod();
        btnPlayMethod();
        btnNextMethod();
    }

    private void nextMethod() {
        onTrackNext();
    }

    private void prevMethod() {
        onTrackPrev();
    }

    private void btnPrevMethod() {
        Thread prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPrev.setOnClickListener(v -> prevMethod());
            }
        };
        prevThread.start();
    }

    private void btnPlayMethod() {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPlay.setOnClickListener(v -> {
                    if (mediaPlayer.isPlaying()) {
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                });
            }
        };
        playThread.start();
        mediaPlayerIsOver();
    }

    private void btnNextMethod() {
        Thread nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnNext.setOnClickListener(v -> nextMethod());
            }
        };
        nextThread.start();
    }

    private void mediaPlayerIsOver() {
        mediaPlayer.setOnCompletionListener(mp -> {
            btnPlay.setImageResource(R.drawable.play_button);
            if (repeat) {
                nextMethod();
            }
        });
    }

    private void setMusicView() {
        musicTitle.setText(musicArrayList.get(position).getTitle());
        musicArtist.setText(musicArrayList.get(position).getArtist());

        Bitmap cover = convertCover(musicArrayList.get(position).getPath());

        if (cover != null) {
            musicCover.setImageBitmap(cover);
        } else {
            musicCover.setImageResource(R.drawable.music_cover);
        }

        seekBarTime.setMax(mediaPlayer.getDuration());
        durationTotal.setText(formattedTime((mediaPlayer.getDuration())));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentDuration = mediaPlayer.getCurrentPosition();

                    seekBarTime.setMax(mediaPlayer.getDuration());
                    seekBarTime.setProgress(currentDuration);
                    durationPlay.setText(formattedTime(currentDuration));
                }

                (new Handler()).postDelayed(this, 1000);
            }
        });
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", position);

        btnShuffle.setOnClickListener(v -> {
            if (shuffle) {
                shuffle = false;
                btnShuffle.setImageResource(R.drawable.shuffle_off);
            } else {
                shuffle = true;
                btnShuffle.setImageResource(R.drawable.shuffle_on);
            }
        });

        btnRepeat.setOnClickListener(v -> {
            if (repeat) {
                repeat = false;
                btnRepeat.setImageResource(R.drawable.repeat_off);
            } else {
                repeat = true;
                btnRepeat.setImageResource(R.drawable.repeat_on);
            }
        });

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        uri = Uri.parse(musicArrayList.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        btnPlay.setImageResource(R.drawable.pause_button);
        mediaPlayer.start();


        setMusicView();

        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private String formattedTime(int time) {
        String result;
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;

        result = minutes + ":";
        if (seconds < 10) {
            result += "0";
        }
        result += seconds;

        return result;
    }

    private int getRandomMusic(int i) {
        Random random = new Random();

        return random.nextInt(i + 1);
    }

    private void initViews() {
        musicTitle = findViewById(R.id.musicTitle);
        musicTitle.setSelected(true);
        musicArtist = findViewById(R.id.musicArtist);
        musicArtist.setSelected(true);
        musicCover = findViewById(R.id.musicCover);
        durationPlay = findViewById(R.id.durationPlay);
        durationTotal = findViewById(R.id.durationTotal);
        btnRepeat = findViewById(R.id.btnRepeat);
        if (repeat) {
            btnRepeat.setImageResource(R.drawable.repeat_on);
        }

        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnShuffle = findViewById(R.id.btnShuffle);
        if (shuffle) {
            btnShuffle.setImageResource(R.drawable.shuffle_on);
        }

        seekBarTime = findViewById(R.id.seekBarTime);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CreateNotification.CHANNEL_ID, "Music Player",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action_name");

            switch (action) {
                case CreateNotification.ACTION_PREV:
                    nextMethod();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mediaPlayer.isPlaying()) {
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }

                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    @Override
    public void onTrackPrev() {
        mediaPlayer.stop();
        mediaPlayer.release();

        if (shuffle) {
            position = getRandomMusic(musicArrayList.size()-1);
        } else {
            position = ((position - 1) < 0 ? (musicArrayList.size() - 1) : (position-1));
        }
        uri = Uri.parse(musicArrayList.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        setMusicView();

        btnPlay.setImageResource(R.drawable.pause_button);
        mediaPlayer.start();
        mediaPlayerIsOver();

        if (isPlaying) {
            CreateNotification.createNotification(this, position, R.drawable.pause_button);
        }
    }

    @Override
    public void onTrackPlay() {
        btnPlay.setImageResource(R.drawable.pause_button);
        mediaPlayer.start();

        if (isPlaying) {
            CreateNotification.createNotification(this, position, R.drawable.pause_button);
        }
    }

    @Override
    public void onTrackPause() {
        btnPlay.setImageResource(R.drawable.play_button);
        mediaPlayer.pause();

        if (isPlaying) {
            CreateNotification.createNotification(this, position, R.drawable.pause_button);
        }
    }

    @Override
    public void onTrackNext() {
        mediaPlayer.stop();
        mediaPlayer.release();

        if (shuffle) {
            position = getRandomMusic(musicArrayList.size() - 1);
        } else {
            position = ((position + 1) % musicArrayList.size());
        }

        uri = Uri.parse(musicArrayList.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        setMusicView();

        btnPlay.setImageResource(R.drawable.pause_button);
        mediaPlayer.start();
        mediaPlayerIsOver();

        if (isPlaying) {
            CreateNotification.createNotification(this, position, R.drawable.pause_button);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        notificationManager.cancelAll();
        unregisterReceiver(broadcastReceiver);
    }
}
