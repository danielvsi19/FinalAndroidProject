package com.example.couplesnight.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.couplesnight.model.CustomMovie
import com.example.couplesnight.model.Preference
import com.example.couplesnight.repository.CustomMovieRepository
import com.example.couplesnight.repository.PreferenceRepository
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceRepository: PreferenceRepository = PreferenceRepository(application)
    private val customMovieRepository: CustomMovieRepository = CustomMovieRepository(application)

    val preferences: LiveData<List<Preference>> = preferenceRepository.getAllPreferences()
    val customMovies: LiveData<List<CustomMovie>> = customMovieRepository.getAllCustomMovies()

    // 🔹 Add or update a preference based on selection
    fun addPreference(name: String, category: String, isSelected: Boolean = true) {
        val preference = Preference(name = name, category = category, isSelected = isSelected)
        viewModelScope.launch {
            preferenceRepository.insertPreference(preference)
        }
    }

    // 🔹 Toggle selection for existing custom movie
    fun toggleCustomMovieSelection(movie: CustomMovie, isSelected: Boolean) {
        val updated = movie.copy(isSelected = isSelected)
        viewModelScope.launch {
            customMovieRepository.updateCustomMovie(updated)
        }
    }

    // 🔹 Save selected preferences
    fun savePreferences() {
        viewModelScope.launch {
            preferences.value?.forEach { pref ->
                preferenceRepository.updatePreference(pref)
            }
        }
    }

    // 🔹 CRUD for custom movies
    fun addCustomMovie(customMovie: CustomMovie) {
        viewModelScope.launch {
            customMovieRepository.insertCustomMovie(customMovie)
        }
    }

    fun updateCustomMovie(customMovie: CustomMovie) {
        viewModelScope.launch {
            customMovieRepository.updateCustomMovie(customMovie)
        }
    }

    fun deleteCustomMovie(customMovie: CustomMovie) {
        viewModelScope.launch {
            customMovieRepository.deleteCustomMovie(customMovie)
        }
    }

    // 🔹 Get predefined preferences
    fun getPredefinedPreferences(): List<Preference> {
        return listOf(
            Preference(name = "Action", category = "Genre"),
            Preference(name = "Comedy", category = "Genre"),
            Preference(name = "Drama", category = "Genre"),
            Preference(name = "Netflix", category = "Platform"),
            Preference(name = "HBO", category = "Platform"),
            Preference(name = "8", category = "Rating"),
            Preference(name = "5", category = "Rating"),
        )
    }
}
