package com.example.musicapp.notification

interface SongNotificationCallback {

    fun onNotificationPlayPauseSong()
    fun onNotificationNextSong()
    fun onNotificationPreviousSong()
    fun onDestroyMusicApp()

}
