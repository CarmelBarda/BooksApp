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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel
import com.colman.mobilePostsApp.databinding.FragmentEditPostBinding
import com.squareup.picasso.Picasso
import java.util.UUID
import kotlin.getValue

class EditPostFragment : Fragment() {
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private var imageBitmap: Bitmap? = null
    private var postId: String? = null
    private var imageUrl: String? = null

    private val args: EditPostFragmentArgs by navArgs()

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
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.submitPostButton.setOnClickListener {
            updatePost()
        }
    }

    private fun loadPostData() {
        BookPostModel.instance.getPostById(postId!!) { post: BookPost? ->
            post?.let {
                binding.bookNameInput.editText?.setText(it.bookName)
                binding.recommendationInput.editText?.setText(it.recommendation)
                binding.bookRatingSlider.value = it.rating.coerceIn(0, 10).toFloat()
                imageUrl = it.bookImage
                Picasso.get().load(imageUrl).into(binding.bookImagePreview)
            }
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageBitmap = getBitmapFromUri(uri)
                binding.bookImagePreview.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun updatePost() {
        val bookName = binding.bookNameInput.editText?.text.toString()
        val recommendation = binding.recommendationInput.editText?.text.toString()
        val rating = binding.bookRatingSlider.value.toInt()

        if (bookName.isEmpty() || recommendation.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.submitPostButton.isEnabled = false

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

    private fun saveUpdatedPost(
        updatedBookName: String,
        updatedRecommendation: String,
        updatedRating: Int,
        updatedImageUrl: String?
    ) {
        val updatedFields = mutableMapOf<String, Any>(
            "bookName" to updatedBookName,
            "recommendation" to updatedRecommendation,
            "rating" to updatedRating,
            "lastUpdated" to System.currentTimeMillis()
        )

        updatedImageUrl?.let { updatedFields["bookImage"] = it }

        if (updatedFields.isNotEmpty()) {
            BookPostModel.instance.updatePost(postId!!, updatedFields) {
                Toast.makeText(requireContext(), "Post updated successfully!", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        } else {
            Toast.makeText(requireContext(), "No changes made!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
