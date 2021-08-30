package com.example.musicapp.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicapp.notification.SongNotification.Companion.NEXT
import com.example.musicapp.notification.SongNotification.Companion.PLAY_PAUSE
import com.example.musicapp.notification.SongNotification.Companion.PREVIOUS
import com.example.musicapp.notification.SongNotification.Companion.CLOSE
import com.example.musicapp.notification.SongNotificationCallback

class SongBroadcast(
    private val songCallback: SongNotificationCallback?
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            NEXT -> songCallback?.onNotificationNextSong()
            PLAY_PAUSE -> songCallback?.onNotificationPlayPauseSong()
            PREVIOUS -> songCallback?.onNotificationPreviousSong()
            CLOSE -> songCallback?.onDestroyMusicApp()
        }
    }
}
