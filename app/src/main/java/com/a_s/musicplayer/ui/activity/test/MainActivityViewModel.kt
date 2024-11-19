package com.a_s.musicplayer.ui.activity.test

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MainActivityEvents() {
    Play,
    Pause,
    SeekToNext,
    SeekToPrevious,
}

//this is just a test viewmodel
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val player: Player, //just a player interface
    private val mediaResolver: MediaResolver
) : ViewModel() {

    init {
        viewModelScope.launch {
            mediaResolver()
            player.prepare()
        }
    }

    private val viewModelUiState = MutableStateFlow(
        Audio(
            uri = player.currentMediaItem?.mediaId?.toUri(),
            title = player.currentMediaItem?.mediaMetadata?.title.toString(),
            artist = player.currentMediaItem?.mediaMetadata?.artist.toString(),
            duration = player.duration,
            position = player.currentPosition,
        )
    )
    val uiState = viewModelUiState.asStateFlow()

    private val isPlaying = flow {
        while (viewModelScope.isActive) {
            emit(player.isPlaying)
            delay(100)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )



    val position = flow {
       while (viewModelScope.isActive) {
           while (isPlaying.value) {
               emit(player.currentPosition.toFloat())
               delay(1000)
           }
           delay(200)
       }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0f
    )




    fun onPositionChange(position : Float) {
        player.seekTo(position.toLong())
    }

    fun events(event : MainActivityEvents) {
        when(event) {
            MainActivityEvents.Play -> {
                player.play()
            }
            MainActivityEvents.Pause -> {
                player.pause()
            }
            MainActivityEvents.SeekToNext -> {
                player.seekToNext()
            }
            MainActivityEvents.SeekToPrevious -> {
                player.seekToPrevious()
            }
        }

        viewModelUiState.update {
            it.copy(
                uri = player.currentMediaItem?.mediaId?.toUri(),
                title = player.currentMediaItem?.mediaMetadata?.title.toString(),
                artist = player.currentMediaItem?.mediaMetadata?.artist.toString(),
                duration = player.duration,
                position = player.currentPosition
            )
        }
    }
}