package com.example.onepicture.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.onepicture.databinding.FragmentProfileBinding
import com.example.onepicture.utils.PicassoHelper
import com.example.onepicture.utils.safeNavigateWithArgs

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModel.provideFactory(
                application = requireActivity().application,
                userId = userId
            )
        )[ProfileViewModel::class.java]


        // Observe the current user data
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameTextView.text = it.name
                binding.emailTextView.text = it.email

                if (it.profileImageUrl != "") {
                    PicassoHelper.loadImage(it.profileImageUrl, binding.profileImageView)
                }
            }
        }

        // Edit button action
        binding.editButton.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment();
            findNavController().safeNavigateWithArgs(action)
        }

        binding.hamburgerIcon.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToPostsFragment()
            findNavController().safeNavigateWithArgs(action)
        }

        binding.cameraIcon.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToCameraFragment()
            findNavController().safeNavigateWithArgs(action)
        }
    }
}