package com.example.kotmod6.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {
    @GET("v2/list")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 40
    ): List<PhotoDto>
}
