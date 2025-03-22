package com.colman.mobilePostsApp.modules

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel
import com.colman.mobilePostsApp.databinding.FragmentCreatePostBinding
import com.colman.mobilePostsApp.modules.BookList.BookSearchFragment
import com.colman.mobilePostsApp.utils.ImageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private var imageBitmap: Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var selectedBookName: String = ""

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = ImageService.registerImagePicker(
            caller = this,
            onImagePicked = { uri ->
                imageBitmap = ImageService.getBitmapFromUri(requireContext().contentResolver, uri)
                binding.bookImagePreview.setImageBitmap(imageBitmap)
            },
            onCancel = {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        )
    }

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

        var selectedRating = 0
        binding.bookRatingSlider.addOnChangeListener { _, value, _ ->
            selectedRating = value.toInt()
            binding.ratingLabel.text = "Rating: $selectedRating"
        }

        binding.selectBookImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.submitPostButton.setOnClickListener {
            val recommendation = binding.recommendationInput.editText?.text.toString()

            val bookSearchFragment = childFragmentManager.findFragmentById(R.id.bookSearchFragment) as? BookSearchFragment
            selectedBookName = bookSearchFragment?.getSelectedBook() ?: ""

            if (selectedBookName.isNotBlank() && recommendation.isNotBlank() && imageBitmap != null) {
                binding.submitPostButton.isEnabled = false
                binding.submitPostButton.text = ""
                binding.postProgressSpinner.visibility = View.VISIBLE

                savePost(selectedBookName, recommendation, selectedRating)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and pick an image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        ImageService.launchImagePicker(imagePickerLauncher)
    }

    private fun loadUserData() {
        user = auth.currentUser
        user?.let {
            binding.userName.text = it.displayName
            val profileImageUrl = it.photoUrl?.toString() ?: ""

            ImageService.loadImage(
                imageUrl = profileImageUrl,
                imageView = binding.profileImage,
                placeholderResId = R.drawable.ic_profile
            )

            binding.profileImage.tag = profileImageUrl
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
