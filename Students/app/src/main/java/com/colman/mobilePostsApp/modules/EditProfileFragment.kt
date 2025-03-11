package com.colman.mobilePostsApp.modules

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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.colman.mobilePostsApp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class EditProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var changePhotoButton: Button

    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImageView)
        nameEditText = view.findViewById(R.id.nameEditText)
        saveButton = view.findViewById(R.id.saveButton)
        changePhotoButton = view.findViewById(R.id.changePhotoButton)

        auth = FirebaseAuth.getInstance()

        loadUserData()

        changePhotoButton.setOnClickListener {
            openGallery()
        }

        saveButton.setOnClickListener {
            saveProfile()
        }
    }

    // 🔹 Load User Data from Firebase Authentication
    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            nameEditText.setText(user.displayName)

            // ✅ Load image from Firebase Authentication profile URL
            if (user.photoUrl != null) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.profile_pic_placeholder)
                    .into(profileImageView)
            }
        }
    }

    // 🔹 Image Picker using ActivityResultContracts.GetContent()
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                profileImageView.setImageURI(it)  // ✅ Display the selected image locally
            }
        }

    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    // 🔹 Save Profile (No Firebase Storage, only Firebase Authentication)
    private fun saveProfile() {
        val newName = nameEditText.text.toString().trim()
        val user = auth.currentUser

        if (user != null) {
            val profileUpdatesBuilder = UserProfileChangeRequest.Builder().setDisplayName(newName)

            if (selectedImageUri != null) {
                // ✅ No Firebase Storage: Just update Authentication's profile URL with the local URI
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
