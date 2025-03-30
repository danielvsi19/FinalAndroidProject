package com.example.couplesnight.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.couplesnight.model.Movie
import com.example.couplesnight.repository.MovieRepository
import kotlinx.coroutines.launch

class FindMoviesViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchMovies(apiKey: String, genres: String, minRating: Double, providers: String, customMovies: List<Movie>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val tmdbMovies = repository.getMovies(apiKey, genres, minRating, providers)
                val merged = (tmdbMovies + customMovies).distinctBy { it.id }
                _movies.value = merged
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
