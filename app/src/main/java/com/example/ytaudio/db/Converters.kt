package com.example.ytaudio.db

import androidx.room.TypeConverter
import com.example.ytaudio.data.streamyt.Thumbnail
import com.example.ytaudio.data.videodata.AudioStream
import com.example.ytaudio.data.videodata.VideoStream
import com.example.ytaudio.data.youtube.Localized
import com.example.ytaudio.data.youtube.YTVideosThumbnails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun toAudioStreamList(value: String): List<AudioStream> {
        val type = object : TypeToken<List<AudioStream>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAudioStreamList(list: List<AudioStream>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toVideoStreamList(value: String): List<VideoStream> {
        val type = object : TypeToken<List<VideoStream>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromVideoStreamList(list: List<VideoStream>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toThumbnail(value: String): Thumbnail =
        Gson().fromJson(value, Thumbnail::class.java)

    @TypeConverter
    fun fromThumbnail(thumbnail: Thumbnail): String =
        Gson().toJson(thumbnail)

    @TypeConverter
    fun toThumbnailList(value: String): List<Thumbnail> {
        val type = object : TypeToken<List<Thumbnail>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromThumbnailList(list: List<Thumbnail>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toYTVideosThumbnails(value: String): YTVideosThumbnails {
        val type = object : TypeToken<YTVideosThumbnails>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromYTVideosThumbnails(thumbnails: YTVideosThumbnails): String =
        Gson().toJson(thumbnails)

    @TypeConverter
    fun toLocalized(value: String): Localized {
        val type = object : TypeToken<Localized>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromLocalized(localized: Localized): String =
        Gson().toJson(localized)

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? =
        Gson().toJson(list)
}