package com.example.musicapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.musicapp.R
import com.example.musicapp.service.SongPlayer
import com.example.musicapp.ui.view.MainActivity


class SongNotification(private val service: SongPlayer) {

    private var notificationManager: NotificationManager? = null
    private var remoteView: RemoteViews? = null

    fun sendNotification() {
        createChannel()
        createRemoteView()
        if (service.isPlaying() == true) remoteView?.setImageViewResource(
            R.id.button_play_pause_notification,
            R.drawable.ic_pause
        )
        else remoteView?.setImageViewResource(
            R.id.button_play_pause_notification,
            R.drawable.ic_play
        )
        val notification = NotificationCompat
            .Builder(service, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music)
            .setCustomBigContentView(remoteView)
            .setSound(null)
            .setDefaults(NUMBER_ZERO)
            .setContentIntent(onOpenMusicApp())
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            service.startForeground(NOTIFICATION_MUSIC_ID, notification)
        } else notificationManager?.notify(NOTIFICATION_MUSIC_ID, notification)
    }

    fun cancelNotification() {
        notificationManager?.cancel(NOTIFICATION_MUSIC_ID)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = service.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createRemoteView() {
        remoteView = RemoteViews(service.packageName, R.layout.view_notification)
        remoteView?.setTextViewText(
            R.id.text_name_song_notification, service.getSongInfo().get(
                NUMBER_ZERO
            )
        )
        remoteView?.setTextViewText(
            R.id.text_name_singer_notification, service.getSongInfo().get(
                NUMBER_ONE
            )
        )
        createPendingIntent(PLAY_PAUSE, R.id.button_play_pause_notification)
        createPendingIntent(NEXT, R.id.button_next_notification)
        createPendingIntent(PREVIOUS, R.id.button_previous_notification)
        createPendingIntent(CLOSE, R.id.button_close_notification)
    }

    private fun createPendingIntent(action: String, viewId: Int) {
        val pendingIntent =
            PendingIntent.getBroadcast(service, BROADCAST_ID, Intent(action), NUMBER_ZERO)
        remoteView?.setOnClickPendingIntent(viewId, pendingIntent)
    }

    private fun onOpenMusicApp(): PendingIntent? {
        val notificationIntent = Intent(service, MainActivity::class.java)
        notificationIntent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        return PendingIntent.getActivity(
            service,
            BROADCAST_ID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        const val CHANNEL_ID = "com.example.musicapp"
        const val PLAY_PAUSE = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val CLOSE = "close"

        const val NUMBER_ZERO = 0
        const val NUMBER_ONE = 1
        const val BROADCAST_ID = 2
        const val NOTIFICATION_MUSIC_ID = 3
    }
}
