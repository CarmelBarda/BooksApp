package com.colman.mobilePostsApp.modules

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import com.colman.mobilePostsApp.utils.ImageService
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var selectedImageURI: Uri? = null

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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        createNewUser()

        binding.btnPickImage.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        ImageService.launchImagePicker(imagePickerLauncher)
    }

    private fun createNewUser() {
        binding.registerButton.setOnClickListener {
            val name = binding.layoutName.editText?.text.toString().trim()
            val email = binding.layoutEmail.editText?.text.toString().trim()
            val password = binding.layoutPassword.editText?.text.toString().trim()

            binding.registerButton.isEnabled = false
            binding.registerButton.text = ""
            binding.postProgressSpinner.visibility = View.VISIBLE

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

                    UserModel.instance.addUser(User(authenticatedUser.uid, name), selectedImageURI!!) {
                        binding.postProgressSpinner.visibility = View.GONE

                        Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }.addOnFailureListener {
                    binding.postProgressSpinner.visibility = View.GONE

                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
