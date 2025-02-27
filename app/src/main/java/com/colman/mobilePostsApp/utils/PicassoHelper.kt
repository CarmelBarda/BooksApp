package com.example.onepicture.utils

import android.widget.ImageView
import com.example.onepicture.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

object PicassoHelper {

    fun loadImage(url: String, imageView: ImageView) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.sample_image) // Add a placeholder image in drawable
            .error(R.drawable.sample_image) // Add an error image in drawable
            .into(imageView)
    }

    fun loadImageWithCache(url: String, imageView: ImageView) {
        Picasso.get()
            .load(url)
            .networkPolicy(NetworkPolicy.OFFLINE) // Try to load from cache first
            .placeholder(R.drawable.sample_image)
            .error(R.drawable.sample_image)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    // Successfully loaded from cache
                }

                override fun onError(e: Exception?) {
                    // Try again online if cache failed
                    Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.sample_image)
                        .error(R.drawable.sample_image)
                        .into(imageView)
                }
            })
    }
}
