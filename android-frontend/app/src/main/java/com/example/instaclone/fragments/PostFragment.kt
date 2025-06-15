package com.example.instaclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instaclone.R
import com.example.instaclone.adapters.ProfileUserPostsAdapter
import com.example.instaclone.adapters.UserPostsAdapter
import com.example.instaclone.databinding.FragmentPostBinding
import com.example.instaclone.tools.Resource
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.viemodels.ImageViewModel
import com.example.instaclone.viemodels.factories.ImageViewModelFactory.Companion.createImageViewModelFactory

class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefManager: SharedPreferencesManager
    private lateinit var adapter: ProfileUserPostsAdapter

    private val viewModel: ImageViewModel by viewModels {
        createImageViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = SharedPreferencesManager(requireContext())
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = ProfileUserPostsAdapter(
            context = requireContext()
        )

        binding.userPostGrid.apply {
            adapter = this@PostFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun observeData() {
        viewModel.userImages.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    adapter.submitList(resource.data)
                }
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getUserImages(prefManager.getUserId()!!.toLong())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
