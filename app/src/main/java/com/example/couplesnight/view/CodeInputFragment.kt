package com.example.couplesnight.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.couplesnight.databinding.FragmentCodeInputBinding
import com.example.couplesnight.viewmodel.CodeViewModel

class CodeInputFragment : Fragment() {

    private var _binding: FragmentCodeInputBinding? = null
    private val binding get() = _binding!!
    private val codeViewModel: CodeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCodeInputBinding.inflate(inflater, container, false)

        binding.validateCodeButton.setOnClickListener {
            val inputCode = binding.codeEditText.text.toString()
            codeViewModel.validateCode(inputCode)
        }

        codeViewModel.codeValidationResult.observe(viewLifecycleOwner) { isValid ->
            if (isValid) {
                Toast.makeText(requireContext(), "Code validated!", Toast.LENGTH_SHORT).show()
                val action = CodeInputFragmentDirections
                    .actionCodeInputFragmentToPreferencesFragment(sessionId = binding.codeEditText.text.toString())
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Invalid code!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
