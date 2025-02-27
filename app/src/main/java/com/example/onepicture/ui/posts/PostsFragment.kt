package com.example.onepicture.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onepicture.databinding.FragmentPostsBinding
import com.example.onepicture.utils.safeNavigateWithArgs

class PostsFragment : Fragment() {
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostsViewModel
    private lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[PostsViewModel::class.java]

        adapter = PostsAdapter (onPostClick = { post ->
            val bundle = Bundle()
            bundle.putInt("postId", post.id)

            val action = PostsFragmentDirections.actionPostsFragmentToPostDetailFragment(post.id)
            findNavController().safeNavigateWithArgs(action, bundle)
        }, {
                post -> viewModel.likePost(post)
        })

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        // Observe ViewModel LiveData
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            posts?.let {
                adapter.submitList(it)
            }
        }
        
        viewModel.likedPost.observe(viewLifecycleOwner, Observer { likedPost ->
            val position = adapter.currentList.indexOfFirst { it.id == likedPost.id }
            if (position != -1) {
                adapter.notifyItemChanged(position)
            }
        })

        binding.cameraIcon.setOnClickListener {
            val action =
                PostsFragmentDirections.actionPostsFragmentToCameraFragment()
            findNavController().safeNavigateWithArgs(action)
        }

        binding.personIcon.setOnClickListener {
            val action =
                PostsFragmentDirections.actionPostsFragmentToProfileFragment()
            findNavController().safeNavigateWithArgs(action)
        }
    }
}
