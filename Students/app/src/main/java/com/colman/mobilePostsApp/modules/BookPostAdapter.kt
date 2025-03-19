import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostModel
import com.squareup.picasso.Picasso
import com.colman.mobilePostsApp.databinding.FragmentBookPostItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.colman.mobilePostsApp.modules.PostsContainerFragmentDirections

class BookPostAdapter(
    private var bookList: List<BookPost>,
    private val navController: NavController
) : RecyclerView.Adapter<BookPostAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: FragmentBookPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: BookPost) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            binding.imageLoadingSpinner.visibility = View.VISIBLE
            binding.bookImage.visibility = View.GONE

            if (book.userId == currentUserId) {
                binding.editButton.visibility = View.VISIBLE
                binding.deleteButton.visibility = View.VISIBLE
            } else {
                binding.editButton.visibility = View.GONE
                binding.deleteButton.visibility = View.GONE
            }

            if (!book.userProfile.isNullOrEmpty()) {
                Picasso.get()
                    .load(book.userProfile)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder)
            }

            if (!book.bookImage.isNullOrEmpty()) {
                Picasso.get()
                    .load(book.bookImage)
                    .error(R.drawable.ic_book_placeholder)
                    .into(binding.bookImage, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            binding.imageLoadingSpinner.visibility = View.GONE
                            binding.bookImage.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            binding.imageLoadingSpinner.visibility = View.GONE
                            binding.bookImage.visibility = View.VISIBLE
                        }
                    })
            } else {
                binding.bookImage.setImageResource(R.drawable.ic_book_placeholder)
                binding.imageLoadingSpinner.visibility = View.GONE
                binding.bookImage.visibility = View.VISIBLE
            }

            binding.userName.text = book.userName
            binding.bookName.text = book.bookName
            binding.recommendationText.text = book.recommendation
            binding.ratingText.text = "${book.rating}/10"

            binding.editButton.setOnClickListener {
                val action = PostsContainerFragmentDirections
                    .actionPostsContainerFragmentToEditPostFragment(book.id)

                navController.navigate(action)
            }

            binding.deleteButton.setOnClickListener {
                deletePost(book)
            }
        }

        private fun deletePost(book: BookPost) {
            BookPostModel.instance.deletePost(book.id) { success ->
                if (success) {
                    Toast.makeText(binding.root.context, "Post deleted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, "Failed to delete post!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = FragmentBookPostItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookViewHolder(binding)
    }
        override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(bookList[position])
    }

    override fun getItemCount() = bookList.size
}
