package com.example.couplesnight.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.couplesnight.databinding.FragmentMutualMoviesBinding
import com.example.couplesnight.model.Movie

class MutualMoviesFragment : Fragment() {

    private var _binding: FragmentMutualMoviesBinding? = null
    private val binding get() = _binding!!
    private val args: MutualMoviesFragmentArgs by navArgs()

    private lateinit var adapter: FindMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMutualMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mutualMovies: List<Movie> = args.mutualMovies.toList()

        if (mutualMovies.isEmpty()) {
            binding.mutualMoviesRecyclerView.visibility = View.GONE
            binding.emptyTextView.visibility = View.VISIBLE
        } else {
            adapter = FindMoviesAdapter(mutualMovies) { _, _ -> }
            binding.mutualMoviesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.mutualMoviesRecyclerView.adapter = adapter
            binding.emptyTextView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}