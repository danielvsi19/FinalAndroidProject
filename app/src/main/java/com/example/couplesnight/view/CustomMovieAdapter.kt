package com.example.couplesnight.view

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.couplesnight.R
import com.example.couplesnight.databinding.ItemCustomMovieBinding
import com.example.couplesnight.model.CustomMovie
import com.squareup.picasso.Picasso

class CustomMovieAdapter(
    private var movies: List<CustomMovie> = emptyList(),
    private val onEditClick: (CustomMovie) -> Unit,
    private val onDeleteClick: (CustomMovie) -> Unit,
    private val onSelectionChanged: (CustomMovie, Boolean) -> Unit
) : RecyclerView.Adapter<CustomMovieAdapter.MovieViewHolder>() {

    fun submitList(newMovies: List<CustomMovie>) {
        movies = newMovies.distinctBy { it.id } // prevent duplicates by unique ID
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemCustomMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movies.size

    inner class MovieViewHolder(private val binding: ItemCustomMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: CustomMovie) {
            binding.customMovieTitleTextView.text = movie.title
            binding.customMovieDescriptionTextView.text = movie.overview

            val imagePath = movie.imagePath
            when {
                imagePath?.startsWith("data:image") == true -> {
                    try {
                        val base64Data = imagePath.substringAfter("base64,")
                        val decodedBytes = Base64.decode(base64Data, Base64.NO_WRAP)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        binding.customMovieImageView.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        binding.customMovieImageView.setImageResource(R.drawable.ic_launcher_background)
                    }
                }
                imagePath?.startsWith("content://") == true || imagePath?.startsWith("file://") == true -> {
                    Picasso.get().load(Uri.parse(imagePath)).into(binding.customMovieImageView)
                }
                imagePath?.startsWith("http") == true -> {
                    Picasso.get().load(imagePath).into(binding.customMovieImageView)
                }
                else -> {
                    binding.customMovieImageView.setImageResource(R.drawable.ic_launcher_background)
                }
            }

            // Click on entire item to toggle selection
            binding.root.setOnClickListener {
                val isSelected = !movie.isSelected
                onSelectionChanged(movie, isSelected)
            }

            binding.editCustomMovieButton.setOnClickListener {
                onEditClick(movie)
            }

            binding.deleteCustomMovieButton.setOnClickListener {
                onDeleteClick(movie)
            }

            // Change background color based on selection state
            binding.root.setBackgroundColor(
                if (movie.isSelected) 0xFF17C3B2.toInt() else 0xFF625290.toInt()
            )
        }
    }
}
