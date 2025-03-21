package com.colman.mobilePostsApp.utils

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.colman.mobilePostsApp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

object ImageLoader {

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
}