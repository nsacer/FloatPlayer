package com.example.floatplayer

import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSession
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class FloatNotificationManager(
    private val context: Context,
    sessionToken: MediaSession.Token,
    listener: PlayerNotificationManager.NotificationListener
) {

    init {

        val controller = MediaController(context, sessionToken)
    }
}