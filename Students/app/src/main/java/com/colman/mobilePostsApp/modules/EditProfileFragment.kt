package com.colman.mobilePostsApp.modules

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserModel
import com.colman.mobilePostsApp.databinding.FragmentEditProfileBinding
import com.colman.mobilePostsApp.utils.ImageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import com.colman.mobilePostsApp.modules.BookPost.BookPostViewModel
import kotlin.getValue

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    private val postsViewModel: BookPostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = ImageService.registerImagePicker(
            caller = this,
            onImagePicked = { uri ->
                selectedImageUri = uri
                binding.profileImageView.setImageURI(selectedImageUri)
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
                placeholderResId = R.drawable.ic_profile
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
            UserModel.instance.updateUser(
                User(user.uid, newName),
                selectedImageUri,
                success = {
                    updateUserProfile(user, newName, user.photoUrl.toString())

                    // posts are relied on users data so they need a refresh
                    postsViewModel.refreshPosts(updatedOnly = false)

                    turnOnSaveButton()
                    Toast.makeText(requireContext(), "Saved changes!", Toast.LENGTH_SHORT).show()
                },
                failure = {
                    turnOnSaveButton()
                    Toast.makeText(requireContext(), "update Failed :(", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun updateUserProfile(user: FirebaseUser, newName: String, imageUrl: String?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .setPhotoUri(imageUrl?.let { Uri.parse(it) })
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.reload().addOnCompleteListener {
                    if (it.isSuccessful) {
                        findNavController().navigate(R.id.action_editProfile_to_userPageFragment)
                    }
                }
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
}
