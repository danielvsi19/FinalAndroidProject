package com.example.couplesnight.repository

import com.example.couplesnight.model.Movie
import com.example.couplesnight.network.RetrofitInstance

class MovieRepository {
    suspend fun getMovies(apiKey: String, genres: String, minRating: Double, providers: String): List<Movie> {
        val response = RetrofitInstance.api.discoverMovies(apiKey, genres, minRating, providers)
        return response.results.map {
            Movie(
                id = it.id,
                title = it.title ?: "",
                overview = it.overview ?: "",
                posterPath = it.posterPath // <-- Important for loading the image
            )
        }
    }
}
