package com.example.instaclone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.instaclone.R
import com.example.instaclone.activities.AddPostActivity
import com.example.instaclone.activities.EditProfileActivity
import com.example.instaclone.activities.SettingActivity
import com.example.instaclone.apiServices.ImageApiService
import com.example.instaclone.apiServices.ProfileImageApiService
import com.example.instaclone.tools.SharedPreferencesManager
import com.example.instaclone.databinding.FragmentProfileBinding
import com.example.instaclone.viemodels.ImageViewModel
import com.example.instaclone.viemodels.factories.ImageViewModelFactory
import com.example.instaclone.viemodels.factories.ImageViewModelFactory.Companion.createImageViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefManager: SharedPreferencesManager
    private val viewModel: ImageViewModel by viewModels {
        createImageViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = SharedPreferencesManager(requireContext())
        setupBinding()
        setupUI()
        setupInitialFragment(savedInstanceState)
        setupMenu()
    }

    private fun setupBinding() {
        binding.apply {
            viewModel = this@ProfileFragment.viewModel
            userId = prefManager.getUserId()!!.toLong()
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun setupUI() {
        binding.apply {
            EditProfileBtn.setOnClickListener {
                startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
            }
            usernameTextView.text = prefManager.getUsername()
            settingBtn.setOnClickListener {
                startActivity(Intent(requireActivity(), SettingActivity::class.java))
            }
        }
    }

    private fun setupInitialFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.profileContainer, PostFragment())
                .commit()
        }
    }

    private fun setupMenu() {
        binding.profileNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.postsBtn -> switchFragment(PostFragment())
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.profileContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}