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
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel
import com.colman.mobilePostsApp.databinding.FragmentEditPostBinding
import java.util.UUID
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.utils.ImageService

class EditPostFragment : Fragment() {
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private var imageBitmap: Bitmap? = null
    private var postId: String? = null
    private var imageUrl: String? = null
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: EditPostFragmentArgs? = arguments?.let { EditPostFragmentArgs.fromBundle(it) }
        postId = safeArgs?.postId

        loadPostData()

        binding.ratingLabel.text = "Rating: ${binding.bookRatingSlider.value.toInt()}"

        binding.bookRatingSlider.addOnChangeListener { _, value, _ ->
            binding.ratingLabel.text = "Rating: ${value.toInt()}"
        }

        binding.selectBookImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.submitPostButton.setOnClickListener {
            updatePost()
        }
    }

    private fun pickImageFromGallery() {
        ImageService.launchImagePicker(imagePickerLauncher)
    }

    private fun loadPostData() {
        BookPostModel.instance.getPostById(postId!!) { post: BookPost? ->
            post?.let {
                selectedBookName = it.bookName
                binding.recommendationInput.editText?.setText(it.recommendation)
                binding.bookRatingSlider.value = it.rating.coerceIn(0, 10).toFloat()
                imageUrl = it.bookImage

                ImageService.loadImage(
                    imageUrl = imageUrl,
                    imageView = binding.bookImagePreview
                )

                val bookSearchFragment = childFragmentManager.findFragmentById(R.id.bookSearchFragment) as? BookSearchFragment
                bookSearchFragment?.setSelectedBook(selectedBookName)
            }
        }
    }

    private fun updatePost() {
        val recommendation = binding.recommendationInput.editText?.text.toString()
        val rating = binding.bookRatingSlider.value.toInt()

        val bookSearchFragment = childFragmentManager.findFragmentById(R.id.bookSearchFragment) as? BookSearchFragment
        selectedBookName = bookSearchFragment?.getSelectedBook() ?: ""

        if (selectedBookName.isEmpty() || recommendation.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageBitmap != null) {
            BookPostModel.instance.saveBookImage(
                imageBitmap!!, UUID.randomUUID().toString() + ".jpg"
            ) { newImageUrl: String ->
                binding.submitPostButton.isEnabled = false
                binding.submitPostButton.text = ""
                binding.postProgressSpinner.visibility = View.VISIBLE

                saveUpdatedPost(recommendation, rating, newImageUrl)
            }
        } else {
            binding.submitPostButton.isEnabled = false
            binding.submitPostButton.text = ""
            binding.postProgressSpinner.visibility = View.VISIBLE
            saveUpdatedPost(recommendation, rating, imageUrl!!)
        }
    }

    private fun saveUpdatedPost(
        updatedRecommendation: String,
        updatedRating: Int,
        updatedImageUrl: String?
    ) {
        val updatedPost = BookPost(
            id = postId!!,
            userId = "",
            bookName = selectedBookName,
            recommendation = updatedRecommendation,
            bookImage = updatedImageUrl,
            rating = updatedRating
        )

        val updatedFields = updatedPost.updateJson

        if (updatedFields.isNotEmpty()) {
            BookPostModel.instance.updatePost(updatedPost.id, updatedFields) {
                binding.submitPostButton.isEnabled = true
                binding.submitPostButton.text = "Save changes"
                binding.postProgressSpinner.visibility = View.GONE
                Toast.makeText(requireContext(), "Post updated successfully!", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        } else {
            binding.submitPostButton.isEnabled = true
            binding.submitPostButton.text = "Save changes"
            binding.postProgressSpinner.visibility = View.GONE
            Toast.makeText(requireContext(), "No changes made!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
