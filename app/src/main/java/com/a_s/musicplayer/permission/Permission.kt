package com.a_s.musicplayer.permission

import androidx.annotation.StringRes

data class Permission(
    val permissions: List<String>,
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val permanentlyDeniedDescription: Int,
    var permissionGranted: Boolean = false,
) {
    fun getDescription(isPermanentlyDenied: Boolean): Int {
        return if (isPermanentlyDenied) permanentlyDeniedDescription else description
    }
}