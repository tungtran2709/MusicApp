package com.example.musicapp.ui

import com.example.musicapp.model.Song

interface SongContract {

    interface View {
        fun showError(error: String)
        fun showAllSongs(songs: List<Song>)
    }

    interface Presenter {
        fun getAllSongs()
    }

}
