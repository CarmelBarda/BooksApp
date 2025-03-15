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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.util.*

class CreatePostFragment : Fragment() {
    private var imageBitmap: Bitmap? = null
    private lateinit var bookImageView: ImageView
    private lateinit var profileImageView: ImageView
    private lateinit var userNameView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImage)
        userNameView = view.findViewById(R.id.userName)
        val bookNameInput: TextInputLayout = view.findViewById(R.id.bookNameInput)
        val recommendationInput: TextInputLayout = view.findViewById(R.id.recommendationInput)
        val submitButton: Button = view.findViewById(R.id.submitPostButton)
        bookImageView = view.findViewById(R.id.bookImagePreview)
        val pickImageButton: Button = view.findViewById(R.id.selectBookImageButton)
        val ratingSeekBar: SeekBar = view.findViewById(R.id.bookRatingSeekBar)
        val ratingLabel: TextView = view.findViewById(R.id.ratingLabel)
        val progressBar: ProgressBar = view.findViewById(R.id.postProgressSpinner)

        auth = FirebaseAuth.getInstance()
        loadUserData()

        var selectedRating = 10
        ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedRating = progress
                ratingLabel.text = "Rating: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 🔹 Image picker logic
        pickImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        submitButton.setOnClickListener {
            val userName = userNameView.text.toString()
            val profileImage = profileImageView.toString()
            val bookName = bookNameInput.editText?.text.toString()
            val recommendation = recommendationInput.editText?.text.toString()

            if (bookName.isNotBlank() && recommendation.isNotBlank() && imageBitmap != null) {
                submitButton.isEnabled = false
                submitButton.text = ""
                progressBar.visibility = View.VISIBLE

                savePost(userName, profileImage, bookName, recommendation, selectedRating, progressBar, submitButton)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and pick an image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            userNameView.text = user.displayName

            if (user.photoUrl != null) {
                Picasso.get()
                    .load(user.photoUrl)
                    .placeholder(R.drawable.profile_pic_placeholder)
                    .into(profileImageView)
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

    private fun savePost(userName: String, profileImage: String, bookName: String,
                         recommendation: String, rate: Int, progressBar: ProgressBar, submitButton: Button) {
        // Ensure there's an image to upload
        if (imageBitmap == null) {
            Toast.makeText(context, "Please select an image!", Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
            submitButton.text = "Create Post"
            progressBar.visibility = View.GONE
            return
        }

        // 🔹 Upload image first
        BookPostModel.instance.saveBookImage(imageBitmap!!, UUID.randomUUID().toString() + ".jpg") { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                val newPost = BookPost(
                    id = UUID.randomUUID().toString(),
                    userName = userName,
                    userProfile = profileImage,
                    bookName = bookName,
                    recommendation = recommendation,
                    bookImage = imageUrl, // ✅ Save the correct image URL
                    rating = rate,
                    lastUpdated = System.currentTimeMillis()
                )

                // 🔹 Now save the post with the correct image URL
                BookPostModel.instance.addPost(newPost) {
                    Toast.makeText(context, "Saved recommendation successfully!", Toast.LENGTH_LONG).show()
                    submitButton.text = "Create Post"
                    submitButton.isEnabled = true
                    progressBar.visibility = View.GONE
                    findNavController().navigateUp()
                }
            } else {
                Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_LONG).show()
                submitButton.text = "Create Post"
                submitButton.isEnabled = true
                progressBar.visibility = View.GONE
            }
        }
    }


}
