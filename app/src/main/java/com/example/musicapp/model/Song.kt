package com.example.musicapp.model

import android.database.Cursor
import android.provider.MediaStore

data class Song(val id: String, val nameSong: String, val nameSinger: String) {

    constructor(cursor: Cursor) : this(
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.ArtistColumns.ARTIST))
    )

}
