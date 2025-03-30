package com.example.couplesnight.view

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.couplesnight.R
import com.example.couplesnight.databinding.ItemFindMovieBinding
import com.example.couplesnight.model.Movie
import com.squareup.picasso.Picasso

class FindMoviesAdapter(
    private val movies: List<Movie>,
    private val onSelectionChanged: (Movie, Boolean) -> Unit
) : RecyclerView.Adapter<FindMoviesAdapter.MovieViewHolder>() {

    private val selectedMovies = mutableSetOf<Movie>()

    inner class MovieViewHolder(private val binding: ItemFindMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitleTextView.text = movie.title
            binding.movieDescriptionTextView.text = movie.overview

            val imagePath = movie.posterPath
            Log.d("IMAGE_DEBUG", "posterPath for '${movie.title}': $imagePath")

            when {
                imagePath?.startsWith("data:image") == true -> {
                    try {
                        val base64Data = imagePath.substringAfter("base64,")
                        val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        binding.movieImageView.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        Log.e("IMAGE_DEBUG", "Failed to decode Base64 image: ${e.message}")
                        binding.movieImageView.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
                !imagePath.isNullOrEmpty() -> {
                    try {
                        val fullImageUrl = "https://image.tmdb.org/t/p/w500$imagePath"
                        Picasso.get()
                            .load(fullImageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(binding.movieImageView)
                    } catch (e: Exception) {
                        Log.e("IMAGE_DEBUG", "Failed to load TMDB image: ${e.message}")
                        binding.movieImageView.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
                else -> {
                    Log.w("IMAGE_DEBUG", "Empty or null image path for '${movie.title}'")
                    binding.movieImageView.setImageResource(R.drawable.ic_launcher_background)
                }
            }

            binding.root.setOnClickListener {
                val isSelected = !selectedMovies.contains(movie)
                if (isSelected) selectedMovies.add(movie) else selectedMovies.remove(movie)
                binding.root.alpha = if (isSelected) 0.6f else 1f
                onSelectionChanged(movie, isSelected)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemFindMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    fun getSelectedMovies(): List<Movie> = selectedMovies.toList()
}
