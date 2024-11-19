package com.a_s.musicplayer.ui.activity.test

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.a_s.musicplayer.ui.activity.test.Query.collection
import com.a_s.musicplayer.ui.activity.test.Query.projection
import com.a_s.musicplayer.ui.activity.test.Query.selection
import com.a_s.musicplayer.utils.Constants.TAG
import com.mpatric.mp3agic.Mp3File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MediaResolver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val player: Player
) {
    suspend operator fun invoke() {

        val mediaItems = mutableListOf<MediaItem>()
//        if (!audioPermissions.permissionGranted) throw Error("audioPermission")

        try {
            withContext(Dispatchers.IO) {
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    null,
                    null
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                    while (cursor.moveToNext()) {
                        val data = cursor.getString(dataColumn)
                        val uri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            cursor.getLong(idColumn)
                        )

                        val mp3File = Mp3File(File(data)) // use this library to get music metadata

                        val audio = when {
                            mp3File.hasId3v1Tag() -> {
                                Audio(
                                    uri = uri,
                                    title = mp3File.id3v1Tag.title,
                                    artist = mp3File.id3v1Tag.artist,
                                )
                            }

                            mp3File.hasId3v2Tag() -> {
                                Audio(
                                    uri = uri,
                                    title = mp3File.id3v2Tag.title,
                                    artist = mp3File.id3v2Tag.artist,
                                )
                            }

                            else -> {
                                Log.d(TAG, "MediaResolver : mp3file has unknown tag")
                                null
                            }
                        }


                        audio?.let { audioNN ->
                            mediaItems.add(MediaItem.Builder()
                                .setUri(audioNN.uri)
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setTitle(audioNN.title)
                                        .setArtist(audioNN.artist)
                                        .build()
                                )
                                .build()
                            )
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    player.setMediaItems(mediaItems)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "message : ${e.message}\ncause : ${e.cause}\n$e")
        }
    }
}


