package com.example.musicapp.data

import com.example.musicapp.model.Song

class SongRepository private constructor(private val local: SongDataSource) : SongDataSource {

    override fun getAllSong(callback: OnDataLocalCallback<List<Song>>) {
        local.getAllSong(callback)
    }

    companion object {
        private var repository: SongRepository? = null
        fun getRepository(local: SongDataSource) =
            repository ?: SongRepository(local).also { repository = it }
    }
}
