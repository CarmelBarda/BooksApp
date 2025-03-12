package com.colman.mobilePostsApp.modules.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.squareup.picasso.Picasso
import com.colman.mobilePostsApp.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var changePhotoButton: Button

    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: EditProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]

        profileImageView = view.findViewById(R.id.profileImageView)
        nameEditText = view.findViewById(R.id.nameEditText)
        saveButton = binding.saveButton
        changePhotoButton = binding.changePhotoButton

        auth = FirebaseAuth.getInstance()

        loadUserData()
        initFields()

        changePhotoButton.setOnClickListener {
            openGallery()
        }

        saveButton.setOnClickListener {
            saveProfile()
            viewModel.updateUser {
//                findNavController().navigate(R.id.action_editMyProfile_to_profile)
//                binding.updateButton.isClickable = true
            }
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            nameEditText.setText(user.displayName)

            if (user.photoUrl != null) {
                Picasso.get()
                    .load(user.photoUrl)
                    .placeholder(R.drawable.profile_pic_placeholder)
                    .into(profileImageView)
            }
        } else {
                Toast.makeText(requireContext(), "Error: No user logged in", Toast.LENGTH_SHORT).show()
                return
            }
    }

    private fun initFields() {
        viewModel.loadUser()

        binding.nameEditText.addTextChangedListener {
            viewModel.name = it.toString().trim()
        }

        viewModel.selectedImageURI.observe(viewLifecycleOwner) { uri ->
            Picasso.get().load(uri).into(binding.profileImageView)
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.nameEditText.setText(user.name)
        }



        viewModel.nameError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.nameEditText.error = it
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                profileImageView.setImageURI(it)

                viewModel.selectedImageURI.postValue(uri)
                viewModel.imageChanged = true
            }
        }

    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun saveProfile() {
        val newName = nameEditText.text.toString().trim()
        val user = auth.currentUser

        if (user != null) {
            val profileUpdatesBuilder = UserProfileChangeRequest.Builder().setDisplayName(newName)

            if (selectedImageUri != null) {
                profileUpdatesBuilder.setPhotoUri(selectedImageUri)
            }

            val profileUpdates = profileUpdatesBuilder.build()

            user.updateProfile(profileUpdates)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
