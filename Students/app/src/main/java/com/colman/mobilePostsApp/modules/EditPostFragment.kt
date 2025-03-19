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
    private var selectedBookName: String = ""

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
            imagePicker.launch("image/*")
        }

        binding.submitPostButton.setOnClickListener {
            updatePost()
        }
    }

    private fun loadPostData() {
        BookPostModel.instance.getPostById(postId!!) { post: BookPost? ->
            post?.let {
                selectedBookName = it.bookName
                binding.recommendationInput.editText?.setText(it.recommendation)
                binding.bookRatingSlider.value = it.rating.coerceIn(0, 10).toFloat()
                imageUrl = it.bookImage
                Picasso.get().load(imageUrl).into(binding.bookImagePreview)

                val bookSearchFragment = childFragmentManager.findFragmentById(R.id.bookSearchFragment) as? BookSearchFragment
                bookSearchFragment?.setSelectedBook(selectedBookName)
            }
        }
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageBitmap = getBitmapFromUri(uri)
            binding.bookImagePreview.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
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
        val updatedFields = mutableMapOf<String, Any>(
            "bookName" to selectedBookName,
            "recommendation" to updatedRecommendation,
            "rating" to updatedRating,
            "lastUpdated" to System.currentTimeMillis()
        )

        updatedImageUrl?.let { updatedFields["bookImage"] = it }

        if (updatedFields.isNotEmpty()) {
            BookPostModel.instance.updatePost(postId!!, updatedFields) {
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
