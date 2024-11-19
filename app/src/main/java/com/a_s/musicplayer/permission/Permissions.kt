package com.a_s.musicplayer.permission

import android.Manifest
import android.os.Build
import com.a_s.musicplayer.R

val audioPermissions = Permission(
    permissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            listOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 -> {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
            )
        }
        else -> {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
            )
        }
    },
    title = R.string.permission_audio_title,
    description = R.string.permission_audio_description,
    permanentlyDeniedDescription = R.string.permission_audio_permanently_denied_description,
)
