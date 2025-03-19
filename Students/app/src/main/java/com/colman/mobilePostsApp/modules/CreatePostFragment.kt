package com.colman.mobilePostsApp.modules

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel
import com.colman.mobilePostsApp.databinding.FragmentCreatePostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import java.util.*

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private var imageBitmap: Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        loadUserData()

        var selectedRating = 10
        binding.bookRatingSlider.addOnChangeListener { _, value, _ ->
            selectedRating = value.toInt()
            binding.ratingLabel.text = "Rating: $selectedRating"
        }

        binding.selectBookImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.submitPostButton.setOnClickListener {
            val bookName = binding.bookNameInput.editText?.text.toString()
            val recommendation = binding.recommendationInput.editText?.text.toString()

            if (bookName.isNotBlank() && recommendation.isNotBlank() && imageBitmap != null) {
                binding.submitPostButton.isEnabled = false
                binding.submitPostButton.text = ""
                binding.postProgressSpinner.visibility = View.VISIBLE

                savePost(bookName, recommendation, selectedRating)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and pick an image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserData() {
        user = auth.currentUser
        user?.let {
            binding.userName.text = it.displayName
            val profileImageUrl = it.photoUrl?.toString() ?: ""

            if (profileImageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(profileImageUrl)
                    .error(com.colman.mobilePostsApp.R.drawable.ic_profile)
                    .into(binding.profileImage)
            }

            binding.profileImage.tag = profileImageUrl
        }
    }

    private fun pickImageFromGallery() {
        imagePicker.launch("image/*")
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageBitmap = getBitmapFromUri(uri)
            binding.bookImagePreview.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        }
    }

    private fun savePost(bookName: String, recommendation: String, rate: Int) {
        if (imageBitmap == null) {
            Toast.makeText(context, "Please select an image!", Toast.LENGTH_SHORT).show()
            binding.submitPostButton.isEnabled = true
            binding.submitPostButton.text = "Create Post"
            binding.postProgressSpinner.visibility = View.GONE
            return
        }

        BookPostModel.instance.saveBookImage(imageBitmap!!, UUID.randomUUID().toString() + ".jpg") { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                val newPost = BookPost(
                    id = UUID.randomUUID().toString(),
                    userId = user?.uid.toString(),
                    bookName = bookName,
                    recommendation = recommendation,
                    bookImage = imageUrl,
                    rating = rate,
                    lastUpdated = System.currentTimeMillis()
                )

                BookPostModel.instance.addPost(newPost) {
                    Toast.makeText(context, "Saved recommendation successfully!", Toast.LENGTH_LONG).show()
                    binding.submitPostButton.text = "Create Post"
                    binding.submitPostButton.isEnabled = true
                    binding.postProgressSpinner.visibility = View.GONE
                    findNavController().navigateUp()
                }
            } else {
                Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_LONG).show()
                binding.submitPostButton.text = "Create Post"
                binding.submitPostButton.isEnabled = true
                binding.postProgressSpinner.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
