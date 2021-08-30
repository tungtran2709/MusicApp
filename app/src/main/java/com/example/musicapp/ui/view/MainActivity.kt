package com.example.musicapp.ui.view

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.musicapp.R
import com.example.musicapp.base.BaseActivity
import com.example.musicapp.broadcast.SongBroadcast
import com.example.musicapp.databinding.ActivityMainBinding
import com.example.musicapp.model.Song
import com.example.musicapp.notification.SongNotification.Companion.CLOSE
import com.example.musicapp.notification.SongNotification.Companion.NEXT
import com.example.musicapp.notification.SongNotification.Companion.PLAY_PAUSE
import com.example.musicapp.notification.SongNotification.Companion.PREVIOUS
import com.example.musicapp.notification.SongNotificationCallback
import com.example.musicapp.service.SongPlayer
import com.example.musicapp.ui.SongContract
import com.example.musicapp.ui.SongPresenter
import com.example.musicapp.ui.adapter.SongAdapter
import com.example.musicapp.utils.Repository
import com.example.musicapp.utils.TimeConvert


class MainActivity : BaseActivity<ActivityMainBinding>(),
    SongContract.View, View.OnClickListener,
    SeekBar.OnSeekBarChangeListener, SongNotificationCallback {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
    private var presenter: SongPresenter? = null
    private var playSongId = -1
    private var songPlayer: SongPlayer? = null
    private val songAdapter = SongAdapter(this::onClickSong)
    private val listSong = mutableListOf<Song>()
    private val handler = Handler()
    private var songBroadcast: SongBroadcast? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SongPlayer.SongBinder
            songPlayer = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            songPlayer?.stopSelf()
        }
    }

    override fun onCreateView() {
        if (checkPermission()) {
            initView()
            initData()
            initAdapter()
            initService()
            initBroadcast()
        } else requestPermission()
    }

    override fun onClick(v: View?) = when (v?.id) {
        R.id.button_play_pause -> playOrPauseSong()
        R.id.button_next -> playNextSong()
        R.id.button_previous -> playPreviousSong()
        else -> Unit
    }

    override fun showAllSongs(songs: List<Song>) {
        this.listSong.addAll(songs)
        songAdapter.updateData(this.listSong)
    }

    override fun showError(error: String) {
        Toast.makeText(this, getString(R.string.error_get_data), Toast.LENGTH_LONG).show()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let {
            changeButtonPlay(true)
            songPlayer?.seekTo(it)
        }
    }

    override fun onNotificationPlayPauseSong() = playOrPauseSong()

    override fun onNotificationNextSong() = playNextSong()

    override fun onNotificationPreviousSong() = playPreviousSong()

    override fun onDestroyMusicApp() {
        songPlayer?.cancelService()
        System.exit(NUMBER_ZERO)
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            )
                return false
        }
        return true
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0
            )
    }

    private fun initData() {
        presenter = SongPresenter(this, Repository.getSongRepository(contentResolver))
        presenter?.getAllSongs()
    }

    private fun initView() {
        binding.run {
            seekBar.setOnSeekBarChangeListener(this@MainActivity)
            listOf(
                buttonPrevious,
                buttonPlayPause,
                buttonNext
            ).forEach {
                it.setOnClickListener(this@MainActivity)
            }
        }
    }

    private fun initAdapter() {
        binding.recyclerSongs.adapter = songAdapter
    }

    private fun initService() {
        songPlayer = SongPlayer()
        bindService(Intent(this, SongPlayer::class.java), connection, BIND_AUTO_CREATE)
    }

    private fun initBroadcast() {
        songBroadcast = SongBroadcast(this)
        IntentFilter().apply {
            addAction(NEXT)
            addAction(PLAY_PAUSE)
            addAction(PREVIOUS)
            addAction(CLOSE)
        }.run {
            registerReceiver(songBroadcast, this)
        }
    }

    private fun onClickSong(song: Song) {
        playSongId = listSong.indexOf(song)
        playThisSong(song)
    }

    private fun playThisSong(song: Song) {
        songPlayer?.createMedia(song)
        playAndInfoSong()
    }

    private fun playAndInfoSong() {
        var currentSong = listSong[playSongId]
        binding.textNameSongPlay.text = currentSong.nameSong
        binding.textNameSingerPlay.text = currentSong.nameSinger
        changeButtonPlay(true)
        songPlayer?.getDuration()?.let {
            binding.textTotalTime.text = TimeConvert.convertMillisecondsToMinute(it)
            binding.seekBar.max = it
        }
    }

    private fun changeButtonPlay(check: Boolean) {
        if (check) {
            songPlayer?.play()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.buttonPlayPause.setImageDrawable(getDrawable(R.drawable.ic_pause))
            }
            updateSeekBar()
        } else {
            songPlayer?.pause()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.buttonPlayPause.setImageDrawable(getDrawable(R.drawable.ic_play))
            }
        }
    }

    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    songPlayer?.getCurrentPosition()?.let {
                        setSeekBar(it)
                        if (songPlayer?.isPlaying() == true) {
                            handler.postDelayed(this, TIME_DELAY)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, TIME_DELAY)
    }

    private fun setSeekBar(time: Int) {
        binding.seekBar.progress = time
        binding.textCurrentTime.text = TimeConvert.convertMillisecondsToMinute(time)
        songPlayer?.let {
            if (time >= it.getDuration() - TIME_EMPTY) {
                playNextSong()
            }
        }
    }

    private fun playOrPauseSong() {
        if (songPlayer?.isPlaying() == true) changeButtonPlay(false)
        else changeButtonPlay(true)
    }

    private fun playNextSong() {
        if (playSongId >= listSong.size - NUMBER_ONE) playSongId = NUMBER_ZERO
        else playSongId++
        playThisSong(listSong[playSongId])
    }

    private fun playPreviousSong() {
        if (playSongId <= NUMBER_ZERO) {
            playSongId = listSong.size - NUMBER_ONE
        } else --playSongId
        playThisSong(listSong[playSongId])
    }

    companion object {
        const val NUMBER_ZERO = 0
        const val NUMBER_ONE = 1
        const val TIME_DELAY = 0L
        const val TIME_EMPTY = 300
    }

}
