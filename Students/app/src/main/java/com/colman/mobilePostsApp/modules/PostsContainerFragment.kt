package com.colman.mobilePostsApp.modules

import BookPostAdapter
import com.colman.mobilePostsApp.viewModels.BookPostViewModel
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.R

class PostsContainerFragment : Fragment(R.layout.fragment_posts_container) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: BookPostAdapter
    private val viewModel: BookPostViewModel by viewModels()

    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = arguments?.getString("userName")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.postsLoadingSpinner)
        recyclerView = view.findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        viewModel.getPosts(userName).observe(viewLifecycleOwner) { bookPosts ->
            adapter = BookPostAdapter(bookPosts, findNavController())
            recyclerView.adapter = adapter

            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        viewModel.refreshPosts()
    }
}
