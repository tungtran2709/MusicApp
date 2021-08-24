package com.example.musicapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.databinding.ItemSongBinding
import com.example.musicapp.model.Song

class SongAdapter(private var onClickSong: (Song) -> Unit) :
    RecyclerView.Adapter<SongViewHolder>() {

    private val songs = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SongViewHolder(
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickSong
        )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bindData(songs[position])
    }

    override fun getItemCount() = songs.size

    fun updateData(songsList: List<Song>) {
        songs.apply {
            clear()
            addAll(songsList)
        }
        notifyDataSetChanged()
    }

}

class SongViewHolder(private val binding: ItemSongBinding, onClickSong: (Song) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {

    private var itemSong: Song? = null

    init {
        binding.root.setOnClickListener {
            itemSong?.let { onClickSong(it) }
        }
    }

    fun bindData(item: Song) {
        itemSong = item
        binding.apply {
            textNameSong.text = item.nameSong
            textNameSinger.text = item.nameSinger
        }
    }

}
