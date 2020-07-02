package com.example.ytaudio.service.library

import android.content.Context
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
import android.support.v4.media.MediaMetadataCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.service.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseAudioSource(context: Context, private val database: AudioDatabaseDao) :
    AbstractAudioSource() {

    private var catalog: List<MediaMetadataCompat> = emptyList()
    private val glide: RequestManager

    init {
        state = STATE_INITIALIZING
        glide = Glide.with(context)
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = catalog.iterator()

    override suspend fun load() {
        updateCatalog(database)?.let {
            catalog = it
            state = STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    private suspend fun updateCatalog(database: AudioDatabaseDao): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val audioInfoList = database.getAllAudioInfo()

            audioInfoList.map {
                MediaMetadataCompat.Builder()
                    .from(it)
                    .apply {
                        displayIconUri = it.thumbnails.maxBy { it.height }!!.uri
                        albumArtUri = it.thumbnails.maxBy { it.height }!!.uri
                    }
                    .build()
            }.toList()
        }
    }
}


fun MediaMetadataCompat.Builder.from(audioInfo: AudioInfo): MediaMetadataCompat.Builder {

    id = audioInfo.youtubeId
    title = audioInfo.title
    artist = audioInfo.author
    duration = audioInfo.durationSeconds.toLong()
    mediaUri = audioInfo.audioStreams.maxBy { it.sampleRate }!!.uri
    displayIconUri = audioInfo.thumbnails.minBy { it.height }!!.uri
    flag = MediaItem.FLAG_PLAYABLE

    downloadStatus = STATUS_NOT_DOWNLOADED

    return this
}
