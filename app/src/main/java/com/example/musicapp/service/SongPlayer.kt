package com.example.musicapp.service

import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import com.example.musicapp.model.Song
import com.example.musicapp.notification.SongNotification
import com.example.musicapp.ui.view.MainActivity


class SongPlayer : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var songNotification: SongNotification? = null
    private var playSong: Song? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        songNotification = SongNotification(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        return SongBinder(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        this.stopSelf()
    }

    fun createMedia(song: Song) {
        playSong = song
        mediaPlayer?.release()
        try {
            val songId = song.id.toLong()
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                songId
            )
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play() {
        mediaPlayer?.start()
        songNotification?.sendNotification()
    }

    fun pause() {
        mediaPlayer?.pause()
        songNotification?.sendNotification()
    }

    fun cancelService() {
        songNotification?.cancelNotification()
    }

    fun seekTo(newPosition: Int) {
        mediaPlayer?.seekTo(newPosition)
    }

    fun getSongInfo() = listOf(playSong?.nameSong, playSong?.nameSinger)

    fun getDuration() = mediaPlayer?.duration ?: 0

    fun getCurrentPosition() = mediaPlayer?.currentPosition

    fun isPlaying() = mediaPlayer?.isPlaying

    class SongBinder(private var service: SongPlayer) : Binder() {
        fun getService(): SongPlayer = service
    }

}
