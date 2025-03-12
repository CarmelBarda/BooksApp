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
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel
import java.util.*

class CreatePostFragment : Fragment() {
    private var imageBitmap: Bitmap? = null
    private lateinit var bookImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileImage: ImageView = view.findViewById(R.id.profileImage)
        val userName: TextView = view.findViewById(R.id.userName)
        val bookNameInput: EditText = view.findViewById(R.id.bookNameInput)
        val recommendationInput: EditText = view.findViewById(R.id.recommendationInput)
        val submitButton: Button = view.findViewById(R.id.submitPostButton)
        bookImageView = view.findViewById(R.id.bookImagePreview)
        val pickImageButton: Button = view.findViewById(R.id.selectBookImageButton)

        userName.text = "John Doe"  // Placeholder username
        profileImage.setImageResource(R.drawable.ic_student_placeholder)

        // 🔹 Image picker logic
        pickImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        submitButton.setOnClickListener {
            val bookName = bookNameInput.text.toString()
            val recommendation = recommendationInput.text.toString()

            if (bookName.isNotBlank() && recommendation.isNotBlank() && imageBitmap != null) {
                submitButton.isEnabled = false
                savePost(bookName, recommendation)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and pick an image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                imageBitmap = getBitmapFromUri(uri)
                bookImageView.setImageBitmap(imageBitmap)
            }
        }
    }

    // 🔹 Convert Uri to Bitmap
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        }
    }

    private fun savePost(bookName: String, recommendation: String) {
        val newPost = BookPost(
            id = UUID.randomUUID().toString(),
            userName = "John Doe",
            userProfile = null,  // Set if needed
            bookName = bookName,
            recommendation = recommendation,
            bookImage = "",  // Will be updated after image upload
            rating = 0,
            lastUpdated = System.currentTimeMillis()
        )

        BookPostModel.instance.addPost(newPost, imageBitmap!!) {
            Toast.makeText(context, "Saved recommendation successfully!", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }
}
