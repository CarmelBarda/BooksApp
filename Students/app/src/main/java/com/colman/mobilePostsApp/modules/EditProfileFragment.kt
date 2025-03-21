package com.colman.mobilePostsApp.modules

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.databinding.FragmentEditProfileBinding
import com.colman.mobilePostsApp.utils.ImageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import androidx.activity.result.ActivityResultLauncher


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var imageBitmap: Bitmap? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = ImageService.registerImagePicker(
            caller = this,
            onImagePicked = { uri ->
                imageBitmap = ImageService.getBitmapFromUri(requireContext().contentResolver, uri)
                binding.profileImageView.setImageBitmap(imageBitmap)
            },
            onCancel = {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("profile_images")

        loadUserData()

        binding.changePhotoButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            binding.nameEditText.editText?.setText(user.displayName)

            ImageService.loadImage(
                imageUrl = user.photoUrl.toString(),
                imageView = binding.profileImageView,
                placeholderResId = R.drawable.profile_pic_placeholder
            )
        }
    }

    private fun pickImageFromGallery() {
        ImageService.launchImagePicker(imagePickerLauncher)
    }

    private fun saveProfile() {
        binding.saveButton.isEnabled = false
        binding.saveButton.text = ""
        binding.profileProgressSpinner.visibility = View.VISIBLE

        val newName = binding.nameEditText.editText?.text.toString().trim()
        val user = auth.currentUser

        if (user != null) {
            if (selectedImageUri != null) {
                val imageRef = storageRef.child("${user.uid}.jpg")
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            updateUserProfile(user, newName, uri.toString())
                        }
                    }
                    .addOnFailureListener {
                        turnOnSaveButton()

                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                updateUserProfile(user, newName, user.photoUrl?.toString())
            }
        }
    }

    private fun updateUserProfile(user: FirebaseUser, newName: String, imageUrl: String?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .setPhotoUri(imageUrl?.let { Uri.parse(it) })
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                updateUserInFirestore(user.uid, newName, imageUrl)

                if (isAdded) {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
            .addOnFailureListener {
                turnOnSaveButton()

                if (isAdded) {
                    Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUserInFirestore(userId: String, newName: String, imageUrl: String?) {
        val userMap = mutableMapOf<String, Any>("name" to newName)
        if (imageUrl != null) {
            userMap["profileImage"] = imageUrl
        }

        firestore.collection("users")
            .document(userId)
            .update(userMap)
            .addOnSuccessListener {
                if (isAdded) {
                    turnOnSaveButton()

                    Toast.makeText(requireContext(), "Firestore updated", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                if (isAdded) {
                    turnOnSaveButton()

                    Toast.makeText(requireContext(), "Failed to update Firestore", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun turnOnSaveButton() {
        binding.saveButton.isEnabled = true
        binding.saveButton.text = "Save"
        binding.profileProgressSpinner.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 1001
    }
}
