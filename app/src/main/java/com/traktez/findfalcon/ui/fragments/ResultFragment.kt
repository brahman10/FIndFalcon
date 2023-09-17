package com.traktez.findfalcon.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.traktez.findfalcon.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private lateinit var _binding: FragmentResultBinding
    private val binding get() = _binding

    private val args:ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        binding.result.text = "Success! Congratulations on Finding Falcone. King Shan is mighty pleased.\n\n Time taken: ${args.timeTaken}\nPlanet found: ${args.planetName}"
        return binding.root
    }

}