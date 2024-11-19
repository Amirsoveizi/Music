package com.a_s.musicplayer.service.player

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.a_s.musicplayer.utils.Constants.TAG


class FileObserver(
    handler: Handler,
    private val contentResolver: ContentResolver,
    private val observableUri: List<Uri>,
    private val onChangeDetected: (selfChange: Boolean, uri : Uri?, flags: Int) -> Unit
) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean, uri: Uri?, flags: Int) {
        super.onChange(selfChange, uri, flags)
        onChangeDetected(selfChange, uri, flags)

        //test
        uri?.let {
            contentResolver.query(
                it,
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.DATE_MODIFIED
                ),
                "${MediaStore.Audio.Media.IS_MUSIC} == 1",
                null,
                "${MediaStore.Video.Media.DATE_MODIFIED} ASC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val dateModifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol)
                    val dateAdded = cursor.getString(dateAddedCol)
                    val dateModified = cursor.getString(dateModifiedCol)

                    Log.d(TAG, "id : $id\nname : $name\nadded : $dateAdded\nmodified : $dateModified")
                    Log.e(TAG, "**********************************************************************")
                }
            }
        }

    }

    fun register() {
        observableUri.forEach { uri ->
            contentResolver.registerContentObserver(
                uri,
                true,
                this
            )
        }
    }

    fun unregister() {
        contentResolver.unregisterContentObserver(this)
    }
}
