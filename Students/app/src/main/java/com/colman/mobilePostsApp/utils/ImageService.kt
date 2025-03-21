package com.colman.mobilePostsApp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.colman.mobilePostsApp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

object ImageService {

    fun loadImage(
        imageUrl: String?,
        imageView: ImageView,
        progressBar: ProgressBar? = null,
        placeholderResId: Int = R.drawable.ic_book_placeholder
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            progressBar?.visibility = View.VISIBLE
            Picasso.get()
                .load(imageUrl)
                .error(placeholderResId)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        progressBar?.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        progressBar?.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    }
                })
        } else {
            imageView.setImageResource(placeholderResId)
            progressBar?.visibility = View.GONE
            imageView.visibility = View.VISIBLE
        }
    }

    fun registerImagePicker(
        caller: ActivityResultCaller,
        onImagePicked: (Uri) -> Unit,
        onCancel: (() -> Unit)? = null
    ): ActivityResultLauncher<String> {
        return caller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                onImagePicked(uri)
            } else {
                onCancel?.invoke()
            }
        }
    }

    fun launchImagePicker(launcher: ActivityResultLauncher<String>) {
        launcher.launch("image/*")
    }

    fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }

}