package com.colman.mobilePostsApp.modules

import BookPostAdapter
import com.colman.mobilePostsApp.viewModels.BookPostViewModel
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.R


class PostsContainerFragment : Fragment(R.layout.fragment_posts_container) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookPostAdapter
    private val viewModel: BookPostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe LiveData and update RecyclerView when data changes
        viewModel.bookPosts.observe(viewLifecycleOwner) { bookPosts ->
            adapter = BookPostAdapter(bookPosts)
            recyclerView.adapter = adapter
        }

        viewModel.refreshPosts()
    }
}
