package com.example.ytaudio.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.domain.SearchItem
import com.example.ytaudio.network.ApiService
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.utils.extensions.mapParallel
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SearchRepository(context: Context) {

    private val database = AudioDatabase.getInstance(context).audioDatabaseDao

    private val _searchItemList = MutableLiveData<List<SearchItem>>()
    val searchItemList: LiveData<List<SearchItem>>
        get() = _searchItemList

    private val _autoCompleteList = MutableLiveData<List<String>>()
    val autoCompleteList: LiveData<List<String>>
        get() = _autoCompleteList

    suspend fun setItemsFromResponse(query: String, maxResults: Int = 25) {
        withContext(Dispatchers.IO) {
            val list =
                ApiService.ytService.getYTResponse(query, maxResults).await()
                    .items.map { it.toSearchItem() }
            _searchItemList.postValue(list)
        }
    }

    suspend fun setAutocomplete(query: String) {
        withContext(Dispatchers.IO) {
            val list =
                ApiService.autoCompleteService.getAutoComplete(query).await()
                    .items?.mapNotNull { it.suggestion?.data }

            _autoCompleteList.postValue(list)
        }
    }

    suspend fun addToDatabase(items: List<String>) {
        withContext(Dispatchers.IO) {
            val startTime = System.nanoTime()
            val audioInfoList = items.mapParallel(Dispatchers.IO) {
                try {
                    YTExtractor().extractAudioInfo(it)
                } catch (e: ExtractionException) {
                    Log.e(javaClass.simpleName, "id: $it extraction failed")
                    null
                } catch (e: YoutubeRequestException) {
                    Log.e(javaClass.simpleName, "network failure")
                    null
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, e.toString())
                    null
                }
            }.filterNotNull()

            database.insert(audioInfoList)
            Log.i(
                javaClass.simpleName,
                "${items.size} items added in ${(System.nanoTime() - startTime) / 1e6} ms"
            )
        }
    }
}