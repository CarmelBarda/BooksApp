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
import com.colman.mobilePostsApp.utils.ImageLoader

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

            ImageLoader.loadImage(
                imageUrl = book.userProfile,
                imageView = binding.profileImage,
                placeholderResId = R.drawable.ic_profile_placeholder
            )

            ImageLoader.loadImage(
                imageUrl = book.bookImage,
                imageView = binding.bookImage,
                progressBar = binding.imageLoadingSpinner
            )

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
