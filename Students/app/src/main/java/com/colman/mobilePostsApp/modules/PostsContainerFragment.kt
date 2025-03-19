package com.colman.mobilePostsApp.modules

import BookPostAdapter
import com.colman.mobilePostsApp.viewModels.BookPostViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.databinding.FragmentPostsContainerBinding
import androidx.navigation.fragment.navArgs
import com.colman.mobilePostsApp.data.bookPost.BookPost


class PostsContainerFragment : Fragment(R.layout.fragment_posts_container) {
    private var _binding: FragmentPostsContainerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookPostAdapter
    private val viewModel: BookPostViewModel by viewModels()

//    private val args: PostsContainerFragmentArgs by navArgs()

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val safeArgs: PostsContainerFragmentArgs? = arguments?.let { PostsContainerFragmentArgs.fromBundle(it) }
//        userId = safeArgs?.userId
        userId = arguments?.getString("userId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsContainerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val userId = try {
//            args.userId
//        } catch (e: IllegalArgumentException) {
//            null
//        }

        binding.postsLoadingSpinner.visibility = View.VISIBLE
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.visibility = View.GONE

        viewModel.getPosts(userId).observe(viewLifecycleOwner) { bookPosts ->
            adapter = BookPostAdapter(bookPosts, findNavController())
            binding.recyclerViewPosts.adapter = adapter

            binding.postsLoadingSpinner.visibility = View.GONE
            binding.recyclerViewPosts.visibility = View.VISIBLE
        }

        viewModel.refreshPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
