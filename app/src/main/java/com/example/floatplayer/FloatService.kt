package com.example.floatplayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.IBinder

class FloatService(private val context: Context): Service() {

    private lateinit var mPlaybackState: PlaybackState
    private var mMediaSession: MediaSession
    private var mMediaPlayer: MediaPlayer

    init {

        mPlaybackState = PlaybackState.Builder()
            .setState(PlaybackState.STATE_NONE, 0, 1f)
            .build()

        mMediaSession = MediaSession(context, "FloatService")
        mMediaSession.setCallback(object : MediaSession.Callback() {

            override fun onPlayFromSearch(query: String?, extras: Bundle?) {
                super.onPlayFromSearch(query, extras)
            }

            override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
                super.onPlayFromUri(uri, extras)
            }

            override fun onStop() {
                super.onStop()
            }

            override fun onPause() {
                super.onPause()
            }
        })
        mMediaSession.isActive = true
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
                or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mMediaSession.setPlaybackState(mPlaybackState)

        mMediaPlayer = MediaPlayer()

    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}