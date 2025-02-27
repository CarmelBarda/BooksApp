package com.example.onepicture.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.onepicture.databinding.FragmentEditProfileBinding
import com.example.onepicture.utils.safeNavigateWithArgs
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    // Uri to hold the selected image
    private var selectedImageUri: Uri? = null

    // Registering the activity result launcher for picking images
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.profileImageView.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
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

        // Observe the user data
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameEditText.setText(it.name)
                binding.emailEditText.setText(it.email)
                // Load the existing profile image if available
                it.profileImageUrl.let { imageUrl ->
                    binding.profileImageView.setImageURI(Uri.parse(imageUrl))
                }
            }
        }

        // Handle profile image click to pick an image
        binding.profileImageView.setOnClickListener {
            pickImageFromGallery()
        }

        // Save button action
        binding.saveButton.setOnClickListener {
            val updatedUser = profileViewModel.user.value?.copy(
                name = binding.nameEditText.text.toString(),
                email = binding.emailEditText.text.toString(),
                profileImageUrl = selectedImageUri?.toString()
                    ?: profileViewModel.user.value?.profileImageUrl ?: ""
            )

            lifecycleScope.launch {
                updatedUser?.let {
                    profileViewModel.updateUser(user = it)
                }
            }

            val action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment()
            findNavController().safeNavigateWithArgs(action)
        }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*") // This will open the gallery to pick an image
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}