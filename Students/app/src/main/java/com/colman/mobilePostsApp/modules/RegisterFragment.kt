package com.colman.mobilePostsApp.modules

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserModel
import com.colman.mobilePostsApp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.result.ActivityResult
import androidx.annotation.RequiresExtension
import com.google.android.material.imageview.ShapeableImageView

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private var selectedImageURI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        defineImageSelectionCallBack()
        openGallery()
        createNewUser()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
        binding.btnPickImage.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionCallBack.launch(intent)
        }
    }

    private fun createNewUser() {
        binding.registerButton.setOnClickListener {
            val name = binding.layoutName.editText?.text.toString().trim()
            val email = binding.layoutEmail.editText?.text.toString().trim()
            val password = binding.layoutPassword.editText?.text.toString().trim()

            if(name.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Invalid input. Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Invalid input. Email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password.length < 6) {
                Toast.makeText(requireContext(), "Invalid input. Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(selectedImageURI === null) {
                Toast.makeText(requireContext(), "Invalid input. Please select a profile picture", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    val authenticatedUser = it.user!!

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(selectedImageURI)
                        .setDisplayName(name)
                        .build()

                    authenticatedUser.updateProfile(profileUpdates)

                    UserModel.instance.addUser(
                        User(authenticatedUser.uid, name),
                        selectedImageURI!!
                    ) {
                        Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            try {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    val imageSize = getImageSize(imageUri)
                    val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                    if (imageSize > maxCanvasSize) {
                        Toast.makeText(requireContext(), "Selected image is too large", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedImageURI = imageUri

                        val profileImageView = binding.root.findViewById<ShapeableImageView>(R.id.profileImageView)
                        profileImageView.setImageURI(imageUri)
                    }
                } else {
                    Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error processing result", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
