package com.example.onepicture.ui.posts

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onepicture.R
import com.example.onepicture.data.model.Comment
import com.example.onepicture.data.model.Post
import com.example.onepicture.databinding.FragmentPostDetailBinding
import kotlinx.coroutines.launch
import java.io.File

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostDetailViewModel
    private lateinit var commentAdapter: CommentAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the postId from arguments
        val postId = PostDetailFragmentArgs.fromBundle(requireArguments()).postId

        viewModel = ViewModelProvider(
            this,
            PostDetailViewModel.provideFactory(
                application = requireActivity().application,
                postId = postId
            )
        )[PostDetailViewModel::class.java]

        // Setup the RecyclerView for comments
        commentAdapter = CommentAdapter()
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.commentsRecyclerView.adapter = commentAdapter

        // Observe the LiveData for the post
        viewModel.post.observe(viewLifecycleOwner) { post ->
            post?.let { bindPostToUI(it) }
        }

        // Observe the LiveData for comments
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.submitList(comments)
        }

        // Add a new comment when the send button is clicked
        binding.sendCommentButton.setOnClickListener {
            val sharedPreferences =
                requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val content = binding.commentEditText.text.toString().trim()
            val userName = sharedPreferences.getString("userName", "")

            if (content.isNotEmpty() && userName != null) {
                val newComment = Comment(
                    postId = postId,
                    content = content,
                    userName = userName,
                    timestamp = System.currentTimeMillis()
                )
                lifecycleScope.launch {

                    viewModel.addComment(newComment)
                }
                binding.commentEditText.text.clear()
            }
        }
    }

    private fun bindPostToUI(post: Post) {
        val postImageView: ImageView = requireView().findViewById(R.id.postImageView)
        val timestampTextView: TextView = requireView().findViewById(R.id.timestampTextView)
        val likesTextView: TextView = requireView().findViewById(R.id.likesTextView)
        val commentsTextView: TextView = requireView().findViewById(R.id.commentsTextView)

        // Set the post's timestamp
        val dateFormat = android.text.format.DateFormat.getMediumDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())
        val dateTime = "${timeFormat.format(post.timestamp)} ${dateFormat.format(post.timestamp)}"
        timestampTextView.text = dateTime

        // Set likes and comments
        likesTextView.text = "${post.likes} likes"
        commentsTextView.text = "${post.comments} comments"

        val file = File(post.imageUrl)
        val uri = Uri.fromFile(file)
        postImageView.setImageURI(uri)
    }
}
