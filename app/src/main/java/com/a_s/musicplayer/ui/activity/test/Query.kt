package com.a_s.musicplayer.ui.activity.test

import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object Query {
    const val selection = "${MediaStore.Audio.Media.IS_MUSIC} == 1"

    val projection = arrayOf(
        MediaStore.Audio.Media._ID, //uri
        MediaStore.Audio.Media.DATA,//path
    )

    val collection: Uri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }
}