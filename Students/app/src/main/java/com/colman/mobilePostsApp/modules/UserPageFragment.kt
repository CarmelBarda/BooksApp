package com.colman.mobilePostsApp.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.colman.mobilePostsApp.databinding.FragmentUserPageBinding
import com.google.firebase.auth.FirebaseAuth

class UserPageFragment : Fragment() {

    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid;

        if (userId != null) {
            val postsFragment = PostsContainerFragment().apply {
                arguments = Bundle().apply {
                    putString("userId", userId)
                }
            }

            childFragmentManager.beginTransaction()
                .replace(binding.postsFragmentContainer.id, postsFragment)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
