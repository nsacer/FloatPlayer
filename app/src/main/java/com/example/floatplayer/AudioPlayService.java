package com.example.floatplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

public class AudioPlayService extends Service {

    public static final String TAG_SESSION = "sessionTag";
    private PlaybackStateCompat playbackStateCompat;
    private MediaSessionCompat mediaSessionCompat;
    private MediaPlayer mediaPlayer;
    private MediaControllerCompat mediaControllerCompat;
    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
        }

        @Override
        public void onPrepare() {
            super.onPrepare();
        }

        @Override
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            super.onPrepareFromUri(uri, extras);
        }

        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onStop() {
            super.onStop();
        }
    };

    private void initService(Context context) {

        //播放状态
        playbackStateCompat = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
                .build();

        mediaSessionCompat = new MediaSessionCompat(context, TAG_SESSION);
        mediaSessionCompat.setCallback(mediaSessionCallback);
        mediaSessionCompat.setActive(true);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setPlaybackState(playbackStateCompat);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });

        mediaControllerCompat = new MediaControllerCompat(context, mediaSessionCompat.getSessionToken());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
