package com.example.musicapp.service;

import static com.example.musicapp.MusicApp.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicapp.PlayerActivity;
import com.example.musicapp.R;
import com.example.musicapp.model.Song;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MyService extends Service {
    private ExoPlayer exoPlayer;
    private boolean isPlaying;
    private Song mSong;
    int mCurrentPosition;



    private MyBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public MyService getMyService() {
            return MyService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MyService", "My Service onBind");

        return myBinder;
    }

    @Override
    public void onCreate() {
        Log.e("MyService", "My Service onCreate");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        release();

        Log.e("MyService", "My Service onStartCommand");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get("object_song");
            mSong = song;

            // Check if music is already playing
            if (!isPlaying) {
                startMusic(song);
            }
        }
        int actionMusic = intent.getIntExtra("action_music_service", 0);

        return START_NOT_STICKY;
    }


    private void startMusic(Song song) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(this).build();
            PlayMusic(song);
            sendNotification(song);
        }

        isPlaying = true;
    }

    public void pauseMusic() {
        if (exoPlayer != null && isPlaying) {
            exoPlayer.pause();
            isPlaying = false;
        }
    }

    public void resumeMusic() {
        if (exoPlayer != null && !isPlaying) {
            exoPlayer.play();
            isPlaying = true;
        }
    }

    public void release() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
            isPlaying = false;
        }
    }

    public void seekTo(int x) {
        exoPlayer.seekTo(x);
    }

    private void PlayMusic(Song song) {
        ZingMp3Api zingMp3Api = new ZingMp3Api(getApplicationContext(), "1.6.34", "https://zingmp3.vn", "2aa2d1c561e809b267f3638c4a307aab", "88265e23d4284f25963e6eedac8fbfa3", String.valueOf(System.currentTimeMillis() / 1000));

        // Define a callback to handle the result of the asynchronous operation
        ZingMp3Api.SongUriCallback callback = new ZingMp3Api.SongUriCallback() {
            @Override
            public void onSuccess(String songUri) {

                // Check if the song URI is not empty
                if (songUri != null && !songUri.isEmpty()) {
                    // Create a Uri from the source URL
                    Uri uri = Uri.parse(songUri);

                    Log.d("url", uri.toString());

                    // Create a MediaItem with the Uri and specify the media content type
                    MediaItem mediaItem = new MediaItem.Builder()
                            .setUri(uri)
                            .setMimeType("audio/*") // Set the MIME type according to your media content type
                            .build();

                    exoPlayer.setMediaItem(mediaItem);
                    exoPlayer.prepare();
                    exoPlayer.setPlayWhenReady(true);
                } else {
                    // Handle the case when the song URI is empty
                    Log.e("PlayMusic", "Empty song URI");
                }
            }

            @Override
            public void onFailure(String error) {
                // Handle the failure case
                Log.e("PlayMusic", "Error: " + error);
            }
        };

        // Call the asynchronous method to get the song URI
        zingMp3Api.getSongURI(song.getId(), callback);
    }


    private void sendNotification(Song song) {
        RemoteViews remoteViewsSmall = getRemoteViews(song, R.layout.layout_custom_notification);
        RemoteViews remoteViewsBig = getRemoteViews(song, R.layout.layout_custom_notification_big);

        // Assign PendingIntents to buttons for small notification
        remoteViewsSmall.setOnClickPendingIntent(R.id.img_play, getPendingIntent(this, 1));
        remoteViewsSmall.setOnClickPendingIntent(R.id.img_previous, getPendingIntent(this, 2));
        remoteViewsSmall.setOnClickPendingIntent(R.id.img_next, getPendingIntent(this, 3));

        // Assign PendingIntents to buttons for big notification
        remoteViewsBig.setOnClickPendingIntent(R.id.img_play, getPendingIntent(this, 1));
        remoteViewsBig.setOnClickPendingIntent(R.id.img_previous, getPendingIntent(this, 2));
        remoteViewsBig.setOnClickPendingIntent(R.id.img_next, getPendingIntent(this, 3));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_artist)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCustomContentView(remoteViewsSmall)
                .setCustomBigContentView(remoteViewsBig)
                .setOngoing(true)
                .build();


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManagerCompat.notify(1, notification);
            startForeground(1, notification);
        }

    @NonNull
    private RemoteViews getRemoteViews(Song song, int layoutResourceId) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), layoutResourceId);

        // Initialize views
        remoteViews.setTextViewText(R.id.tv_title_song, song.getName_song());
        remoteViews.setTextViewText(R.id.tv_single_song, song.getName_artist());
        if (layoutResourceId == R.id.layout_custom_notification_big) {
            // Load image into ImageView
            remoteViews.setImageViewBitmap(R.id.img_thumbnail, getBitmapFromUrl(song.getThumbnail()));
        }

        // Set the play/pause button
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_pause);
        } else {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_play);
        }

        return remoteViews;
    }
    private Bitmap getBitmapFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private PendingIntent getPendingIntent(Context context, int action){
            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("action_music_service", action);

            return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_IMMUTABLE);
        }

        @Override
        public boolean onUnbind(Intent intent) {
            Log.e("MyService", "My Service onUnbind");

            return super.onUnbind(intent);
        }

        @Override
        public void onDestroy() {
            Log.e("MyService", "My Service onDestroy");

            if (exoPlayer != null){
                exoPlayer.release();
                exoPlayer = null;
            }


            super.onDestroy();
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public ExoPlayer getExoPlayer() {
            return exoPlayer;
        }

        public int getCurrentPosition() {
            return (int) exoPlayer.getCurrentPosition()/1000;
        }
    }
