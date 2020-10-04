package com.nvvi9.ytaudio.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class YouTubeRepository @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytStream: YTStream,
) {

    fun getSearchResponse(query: String): Flow<PagingData<YTVideoDetails>> =
        Pager(PagingConfig(PAGE_SIZE)) {
            YTSearchPagingSource(query, ytApiService, ytStream)
        }.flow

    fun getPopularResponse() =
        Pager(PagingConfig(PAGE_SIZE)) {
            YTVideoDetailsPagingSource(ytApiService, ytStream)
        }.flow

    companion object {
        private const val PAGE_SIZE = 7
    }
}