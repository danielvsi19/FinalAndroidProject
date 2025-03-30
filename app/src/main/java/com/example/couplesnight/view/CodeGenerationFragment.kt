package com.example.couplesnight.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.couplesnight.databinding.FragmentCodeGenerationBinding
import com.example.couplesnight.viewmodel.CodeViewModel

class CodeGenerationFragment : Fragment() {

    private var _binding: FragmentCodeGenerationBinding? = null
    private val binding get() = _binding!!
    private val codeViewModel: CodeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCodeGenerationBinding.inflate(inflater, container, false)

        codeViewModel.generatedCode.observe(viewLifecycleOwner) { code ->
            binding.generatedCodeTextView.text = code
        }

        binding.generateCodeButton.setOnClickListener {
            codeViewModel.generateCode()
        }
        binding.nextButton.setOnClickListener {
            val action = CodeGenerationFragmentDirections.actionCodeGenerationFragmentToCodeInputFragment()
            it.findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}