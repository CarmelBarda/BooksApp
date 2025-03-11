package com.colman.mobilePostsApp.modules

import BookPostAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.MainActivity
import com.colman.mobilePostsApp.R


class PostsContainerFragment : Fragment(R.layout.fragment_posts_container) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookPostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = BookPostAdapter(MainActivity.Companion.bookPosts)
        recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}

