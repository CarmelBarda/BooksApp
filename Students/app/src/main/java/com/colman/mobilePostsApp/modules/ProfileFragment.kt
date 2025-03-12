package com.colman.mobilePostsApp.modules

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.colman.mobilePostsApp.R
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImageView)
        userNameTextView = view.findViewById(R.id.userNameTextView)

        loadUserData()
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userNameTextView.text = user.displayName ?: "No Name"

            // Load the profile image from Firebase Storage URL
            if (user.photoUrl != null) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.profile_pic_placeholder)
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.profile_pic_placeholder)
            }
        } else {
            userNameTextView.text = "User not logged in"
            profileImageView.setImageResource(R.drawable.profile_pic_placeholder)
        }
    }

}
