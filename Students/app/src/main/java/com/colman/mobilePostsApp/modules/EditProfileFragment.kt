package com.colman.mobilePostsApp.modules

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.databinding.FragmentEditProfileBinding
import com.colman.mobilePostsApp.viewModels.UserViewModel
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.loadCurrentUser()

        userViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                binding.nameEditText.editText?.setText(user.name)
                user.profileImage?.let {
                    Picasso.get().load(it)
                        .placeholder(R.drawable.profile_pic_placeholder)
                        .into(binding.profileImageView)
                }
            }
        })

        binding.changePhotoButton.setOnClickListener {
            openGallery()
        }

        binding.saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.profileImageView.setImageURI(uri)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        imagePicker.launch("image/*")
    }

    private fun saveProfile() {
        binding.saveButton.isEnabled = false
        binding.nameEditText.visibility = View.GONE
        binding.profileProgressSpinner.visibility = View.VISIBLE

        val newName = binding.nameEditText.editText?.text.toString().trim()

        userViewModel.updateProfile(newName, selectedImageUri) { success ->
            binding.saveButton.isEnabled = true
            binding.profileProgressSpinner.visibility = View.GONE
            binding.nameEditText.visibility = View.GONE

            if (success) {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
