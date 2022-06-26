package com.herdaynote.musicplayer;

import static com.herdaynote.musicplayer.ListMusic.convertCover;
import static com.herdaynote.musicplayer.ListMusic.musicArrayList;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.herdaynote.musicplayer.Services.NotificationActionService;

public class CreateNotification {
    public static final String CHANNEL_ID = "Music Notification";
    public static final String ACTION_PREV = "action_prev";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";
    public static Notification notification;

    @SuppressLint("UnspecifiedImmutableFlag")
    public static void createNotification(Context context, int position, int play_btn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(
                    context
            );
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Bitmap icon = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.music_cover_small
            );

            if (convertCover(musicArrayList.get(position).getPath()) != null) {
                icon = convertCover(musicArrayList.get(position).getPath());
            }

            int prev_btn = R.drawable.ic_prev;
            int next_btn = R.drawable.ic_next;

            PendingIntent pendingIntentPrev;
            Intent intentPrev = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PREV);
            pendingIntentPrev = PendingIntent.getBroadcast(
                    context, 0, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT
            );

            PendingIntent pendingIntentPlay;
            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            pendingIntentPlay = PendingIntent.getBroadcast(
                    context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
            );

            PendingIntent pendingIntentNext;
            Intent intentNext = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(
                    context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT
            );

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(musicArrayList.get(position).getTitle())
                    .setContentText(musicArrayList.get(position).getArtist())
                    .setSmallIcon(R.drawable.music_cover_small)
                    .setLargeIcon(icon)
                    .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(false)
                    .setShowWhen(false)
                    .addAction(prev_btn, "Prev", pendingIntentPrev)
                    .addAction(play_btn, "Play", pendingIntentPlay)
                    .addAction(next_btn, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                    )
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}
