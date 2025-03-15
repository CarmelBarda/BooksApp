package com.colman.mobilePostsApp.modules

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.colman.mobilePostsApp.databinding.FragmentEditPostBinding
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel
import com.colman.mobilePostsApp.databinding.FragmentRegisterBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.util.UUID

class EditPostFragment : Fragment() {
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private var imageBitmap: Bitmap? = null
    private var postId: String? = null
    private var imageUrl: String? = null
    private var bookImageView: ImageView? = null
    private var bookNameInput: TextInputLayout? = null
    private var recommendationInput: TextInputLayout? = null
    private var ratingSlider: Slider? = null
    private var submitButton: MaterialButton? = null
    private var pickImageButton: MaterialButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookImageView = view.findViewById(R.id.bookImagePreview)
        bookNameInput = view.findViewById(R.id.bookNameInput)
        recommendationInput = view.findViewById(R.id.recommendationInput)
        ratingSlider = view.findViewById(R.id.bookRatingSlider)
        submitButton = view.findViewById(R.id.submitPostButton)
        pickImageButton = view.findViewById(R.id.selectBookImageButton)

        postId = requireArguments().getString("postId")
        loadPostData()

        binding.selectBookImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.submitPostButton.setOnClickListener {
            updatePost()
        }
    }

    private fun loadPostData() {
        BookPostModel.instance.getPostById(postId!!) { post: BookPost? ->
            if (post != null) {
                bookNameInput!!.editText?.setText(post.bookName)
                recommendationInput!!.editText?.setText(post.recommendation)
                val validRating = post.rating.coerceIn(0, 10)
                ratingSlider!!.value = validRating.toFloat()
                imageUrl = post.bookImage
                Picasso.get().load(imageUrl).into(bookImageView)
            }
        }
    }

    private val imagePickerLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                imageBitmap = getBitmapFromUri(uri)
                bookImageView?.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                return ImageDecoder.decodeBitmap(source)
            } else {
                return MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun updatePost() {
        val bookName = bookNameInput!!.editText?.text.toString()
        val recommendation = recommendationInput!!.editText?.text.toString()
        val rating = ratingSlider!!.value.toInt()

        if (bookName.isEmpty() || recommendation.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        submitButton!!.isEnabled = false

        if (imageBitmap != null) {
            BookPostModel.instance.saveBookImage(
                imageBitmap!!, UUID.randomUUID().toString() + ".jpg"
            ) { newImageUrl: String ->
                saveUpdatedPost(bookName, recommendation, rating, newImageUrl)
            }
        } else {
            saveUpdatedPost(bookName, recommendation, rating, imageUrl!!)
        }
    }

    private fun saveUpdatedPost(updatedBookName: String, updatedRecommendation: String,
                                updatedRating: Int, updatedImageUrl: String?) {
        val updatedFields = mutableMapOf<String, Any>()

        updatedFields["bookName"] = updatedBookName
        updatedFields["recommendation"] = updatedRecommendation
        updatedFields["rating"] = updatedRating
        updatedFields["lastUpdated"] = System.currentTimeMillis()

        if (updatedImageUrl != null) {
            updatedFields["bookImage"] = updatedImageUrl
        }

        if (updatedFields.isNotEmpty()) {
            BookPostModel.instance.updatePost(postId!!, updatedFields) {
                Toast.makeText(requireContext(), "Post updated successfully!", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        } else {
            Toast.makeText(requireContext(), "No changes made!", Toast.LENGTH_SHORT).show()
        }
    }
}