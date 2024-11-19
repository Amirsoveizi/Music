package com.a_s.musicplayer.ui.activity.test

import android.net.Uri

data class Audio(
    val uri: Uri?, //id
    val title: String,
    val artist: String,
    val duration: Long = 0,
    val position: Long = 0,
)
