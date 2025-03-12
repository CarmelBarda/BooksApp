package com.colman.mobilePostsApp.modules

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.colman.mobilePostsApp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var changePhotoButton: Button

    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
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
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("profile_images")

        loadUserData()

        changePhotoButton.setOnClickListener {
            openGallery()
        }

        saveButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            nameEditText.setText(user.displayName)

            if (user.photoUrl != null) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.profile_pic_placeholder)
                    .into(profileImageView)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveProfile() {
        val newName = nameEditText.text.toString().trim()
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

        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .update(userMap)
            .addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Firestore updated", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to update Firestore", Toast.LENGTH_SHORT).show()
                }
            }
    }


    companion object {
        private const val IMAGE_PICK_REQUEST = 1001
    }
}
