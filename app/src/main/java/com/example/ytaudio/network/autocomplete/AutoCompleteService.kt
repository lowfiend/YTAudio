package com.example.ytaudio.network.autocomplete

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface AutoCompleteService {

    @GET("search")
    fun getAutoComplete(
        @Query("q") q: String,
        @Query("ds") ds: String = "yt",
        @Query("client") client: String = "toolbar"
    ): Deferred<AutoComplete>
}