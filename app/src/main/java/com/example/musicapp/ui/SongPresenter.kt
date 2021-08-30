package com.example.musicapp.ui

import com.example.musicapp.data.OnDataLocalCallback
import com.example.musicapp.data.SongRepository
import com.example.musicapp.model.Song

class SongPresenter(
    private val view: SongContract.View,
    private val repository: SongRepository
) : SongContract.Presenter {

    override fun getAllSongs() {
        repository.getAllSong(object : OnDataLocalCallback<List<Song>> {
            override fun onSucceed(data: List<Song>?) {
                if (data != null) {
                    view.showAllSongs(data)
                }
            }

            override fun onFailed(e: Exception?) {
                view.showError(e?.message.toString())
            }

        })
    }

}
