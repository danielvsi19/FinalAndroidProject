package com.example.couplesnight.view

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.couplesnight.R
import com.example.couplesnight.databinding.FragmentPreferencesBinding
import com.example.couplesnight.model.CustomMovie
import com.example.couplesnight.model.Preference
import com.example.couplesnight.repository.MatchRepository
import com.example.couplesnight.viewmodel.PreferencesViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.InputStream

class PreferencesFragment : Fragment(R.layout.fragment_preferences) {

    private lateinit var binding: FragmentPreferencesBinding
    private val preferencesViewModel: PreferencesViewModel by viewModels()
    private lateinit var customMovieAdapter: CustomMovieAdapter
    private val matchRepository = MatchRepository()
    private val args: PreferencesFragmentArgs by navArgs()

    private var selectedImageUri: Uri? = null
    private var selectedBase64Image: String? = null
    private var editingMovie: CustomMovie? = null
    private val selectedPreferences = mutableSetOf<Preference>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreferencesBinding.bind(view)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val sessionId = args.sessionId

        customMovieAdapter = CustomMovieAdapter(
            onDeleteClick = { movie -> preferencesViewModel.deleteCustomMovie(movie) },
            onEditClick = { movie ->
                editingMovie = movie
                binding.customMovieTitleEditText.setText(movie.title)
                binding.customMovieDescriptionEditText.setText(movie.overview)
                selectedImageUri = null
                selectedBase64Image = movie.imagePath
                loadImageFromPath(movie.imagePath, binding.movieImageView)
            },
            onSelectionChanged = { movie, isSelected ->
                preferencesViewModel.toggleCustomMovieSelection(movie, isSelected)
            }
        )

        binding.customMoviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = customMovieAdapter
        }

        val predefinedPreferences = preferencesViewModel.getPredefinedPreferences()
        binding.preferencesCheckboxContainer.removeAllViews()
        predefinedPreferences.forEach { pref ->
            val checkBox = CheckBox(requireContext()).apply {
                text = "${pref.category}: ${pref.name}"
                isChecked = pref.isSelected
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedPreferences.add(pref)
                    else selectedPreferences.remove(pref)
                    preferencesViewModel.addPreference(pref.name, pref.category, isChecked)
                }
            }
            binding.preferencesCheckboxContainer.addView(checkBox)
        }

        preferencesViewModel.customMovies.observe(viewLifecycleOwner) { movies ->
            customMovieAdapter.submitList(movies.toList())
        }

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.addCustomMovieButton.setOnClickListener {
            val title = binding.customMovieTitleEditText.text.toString()
            val overview = binding.customMovieDescriptionEditText.text.toString()

            if (title.isNotBlank() && (selectedImageUri != null || selectedBase64Image != null)) {
                if (selectedImageUri != null) {
                    try {
                        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(selectedImageUri!!)
                        val imageBytes = inputStream?.readBytes()
                        selectedBase64Image = imageBytes?.let {
                            "data:image/png;base64," + Base64.encodeToString(it, Base64.NO_WRAP)
                        }
                    } catch (e: Exception) {
                        Log.e("ImageConversion", "Failed to convert image to Base64: ${e.message}")
                        Toast.makeText(requireContext(), "Image conversion failed", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                val movie = CustomMovie(
                    id = editingMovie?.id ?: System.currentTimeMillis().toInt(),
                    title = title,
                    overview = overview,
                    imagePath = selectedBase64Image,
                    isSelected = true
                )

                if (editingMovie != null) {
                    preferencesViewModel.updateCustomMovie(movie)
                    Toast.makeText(requireContext(), "Movie updated!", Toast.LENGTH_SHORT).show()
                } else {
                    preferencesViewModel.addCustomMovie(movie)
                    Toast.makeText(requireContext(), "Movie added!", Toast.LENGTH_SHORT).show()
                }

                clearMovieForm()
            } else {
                Toast.makeText(requireContext(), "Title and image required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.savePreferencesButton.setOnClickListener {
            preferencesViewModel.savePreferences()
            Toast.makeText(requireContext(), "Preferences saved!", Toast.LENGTH_SHORT).show()
        }

        binding.findMoviesButton.setOnClickListener {
            Log.d("FIREBASE", "sessionId = $sessionId")

            val genres = selectedPreferences.filter { it.category == "Genre" }
                .joinToString(",") { it.name }
            val providers = selectedPreferences.filter { it.category == "Platform" }
                .joinToString(",") { it.name }
            val minRating = selectedPreferences.find { it.category == "Rating" }
                ?.name?.replace("+", "")?.toDoubleOrNull() ?: 0.0
            val selectedCustomMovies = preferencesViewModel.customMovies.value
                ?.filter { it.isSelected } ?: emptyList()

            lifecycleScope.launch {
                Toast.makeText(requireContext(), "Waiting for your partner...", Toast.LENGTH_SHORT).show()

                matchRepository.setUserReady(
                    sessionId, userId, genres, minRating, providers, selectedCustomMovies
                )

                val result = matchRepository.waitForMatch(sessionId)

                val combinedGenres = result["genres"] as? String ?: ""
                val combinedRating = (result["minRating"] as? Number)?.toFloat() ?: 0f
                val combinedProviders = result["providers"] as? String ?: ""
                val combinedCustomMovies = (result["customMovies"] as? List<CustomMovie>)
                    ?.distinctBy { it.id } ?: emptyList()


                val action = PreferencesFragmentDirections
                    .actionPreferencesFragmentToFindMoviesFragment(
                        apiKey = "527d7767d8068a9422052f4a90de30c7",
                        genres = combinedGenres,
                        minRating = combinedRating,
                        providers = combinedProviders,
                        customMovies = combinedCustomMovies.toTypedArray(),
                        sessionId = sessionId
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun loadImageFromPath(path: String?, imageView: ImageView) {
        when {
            path == null -> imageView.setImageResource(R.drawable.ic_launcher_background)
            path.startsWith("data:image") -> {
                try {
                    val base64Data = path.substringAfter("base64,")
                    val decodedBytes = Base64.decode(base64Data, Base64.NO_WRAP)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imageView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imageView.setImageResource(R.drawable.ic_launcher_background)
                    Log.e("ImageDecode", "Base64 error: ${e.message}")
                }
            }
            else -> imageView.setImageURI(Uri.parse(path))
        }
    }

    private fun clearMovieForm() {
        editingMovie = null
        selectedImageUri = null
        selectedBase64Image = null
        binding.customMovieTitleEditText.text.clear()
        binding.customMovieDescriptionEditText.text.clear()
        binding.movieImageView.setImageResource(R.drawable.ic_launcher_foreground)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                binding.movieImageView.setImageURI(uri)
            }
        }
    }


    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
