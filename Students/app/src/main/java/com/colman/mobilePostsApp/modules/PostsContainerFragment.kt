package com.colman.mobilePostsApp.modules

import BookPostAdapter
import com.colman.mobilePostsApp.viewModels.BookPostViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.databinding.FragmentPostsContainerBinding

class PostsContainerFragment : Fragment(R.layout.fragment_posts_container) {
    private var _binding: FragmentPostsContainerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookPostAdapter
    private val viewModel: BookPostViewModel by viewModels()

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

        binding.postsLoadingSpinner.visibility = View.VISIBLE
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.visibility = View.GONE

        viewModel.bookPosts.observe(viewLifecycleOwner) { bookPosts ->
            adapter = BookPostAdapter(bookPosts) { post ->
                val bundle = Bundle().apply {
                    putString("postId", post.id)
                }
                findNavController().navigate(R.id.action_postsContainerFragment_to_editPostFragment, bundle)
            }
            binding.recyclerViewPosts.adapter = adapter

            binding.postsLoadingSpinner.visibility = View.GONE
            binding.recyclerViewPosts.visibility = View.VISIBLE
        }

        viewModel.refreshPosts()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_posts_container, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add_post -> {
                        findNavController().navigate(R.id.action_postsContainerFragment_to_createPostFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
