import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.squareup.picasso.Picasso
import com.colman.mobilePostsApp.databinding.FragmentBookPostItemBinding

class BookPostAdapter(
    private var bookList: List<BookPost>,
    private val navController: NavController
) : RecyclerView.Adapter<BookPostAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: FragmentBookPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: BookPost) {
            binding.imageLoadingSpinner.visibility = View.VISIBLE
            binding.bookImage.visibility = View.GONE

            if (!book.userProfile.isNullOrEmpty()) {
                Picasso.get()
                    .load(book.userProfile)
                    .error(R.drawable.ic_student_placeholder)
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_student_placeholder)
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
                val bundle = Bundle().apply {
                putString("postId", book.id)
            }
                navController.navigate(R.id.action_postsContainerFragment_to_editPostFragment, bundle) }
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
