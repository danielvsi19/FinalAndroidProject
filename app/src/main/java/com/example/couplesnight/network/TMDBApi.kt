package com.example.couplesnight.network

import com.example.couplesnight.model.TMDBResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApi {
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String?,
        @Query("vote_average.gte") minRating: Double = 0.0,
        @Query("with_watch_providers") providers: String?,
        @Query("watch_region") region: String = "US"
    ): TMDBResponse
}
