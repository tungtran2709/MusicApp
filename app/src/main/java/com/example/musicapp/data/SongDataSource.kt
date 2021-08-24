package com.example.musicapp.data

import com.example.musicapp.model.Song

interface SongDataSource {
    fun getAllSong(callback: OnDataLocalCallback<List<Song>>)
}
