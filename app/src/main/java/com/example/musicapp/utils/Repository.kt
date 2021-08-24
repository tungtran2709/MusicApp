package com.example.musicapp.utils

import android.content.ContentResolver
import com.example.musicapp.data.SongLocalDataSource
import com.example.musicapp.data.SongRepository

object Repository {

    fun getSongRepository(contentResolver: ContentResolver): SongRepository {
        val local = SongLocalDataSource.getRepository(contentResolver)
        return SongRepository.getRepository(local)
    }

}
