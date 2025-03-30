package com.example.couplesnight.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.couplesnight.databinding.FragmentFindMoviesBinding
import com.example.couplesnight.model.Movie
import com.example.couplesnight.repository.MatchRepository
import com.example.couplesnight.viewmodel.FindMoviesViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FindMoviesFragment : Fragment() {

    private var _binding: FragmentFindMoviesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FindMoviesViewModel by viewModels()
    private val args: FindMoviesFragmentArgs by navArgs()
    private val matchRepository = MatchRepository()

    private lateinit var adapter: FindMoviesAdapter
    private val selectedMovies = mutableSetOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FindMoviesAdapter(emptyList()) { movie, isSelected ->
            if (isSelected) selectedMovies.add(movie) else selectedMovies.remove(movie)
        }

        binding.findMoviesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.findMoviesRecyclerView.adapter = adapter

        viewModel.movies.observe(viewLifecycleOwner) { movieList ->
            val customMovies = args.customMovies.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    overview = it.overview,
                    posterPath = it.imagePath ?: ""
                )
            }
            val combinedList = (movieList + customMovies).distinctBy { it.id }
            adapter = FindMoviesAdapter(combinedList) { movie, isSelected ->
                if (isSelected) selectedMovies.add(movie) else selectedMovies.remove(movie)
            }
            binding.findMoviesRecyclerView.adapter = adapter
        }

        binding.submitMoviesButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val sessionId = args.sessionId

            lifecycleScope.launch {
                if (userId != null) {
                    matchRepository.setSelectedMovies(sessionId, userId, selectedMovies.toList())
                }
                Toast.makeText(requireContext(), "Waiting for your partner...", Toast.LENGTH_SHORT).show()

                val mutualMovies = matchRepository.waitForMovieSelection(sessionId)

                val action = FindMoviesFragmentDirections
                    .actionFindMoviesFragmentToMutualMoviesFragment(mutualMovies.toTypedArray())
                findNavController().navigate(action)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        val customMovies = args.customMovies.map {
            Movie(
                id = it.id,
                title = it.title,
                overview = it.overview,
                posterPath = it.imagePath ?: ""
            )
        }

        viewModel.fetchMovies(
            apiKey = args.apiKey,
            genres = args.genres,
            minRating = args.minRating.toDouble(),
            providers = args.providers,
            customMovies = customMovies
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
