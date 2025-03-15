import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.squareup.picasso.Picasso

class BookPostAdapter(
    private var bookList: List<BookPost>,
    private val onItemClick: (BookPost) -> Unit
) : RecyclerView.Adapter<BookPostAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val bookName: TextView = itemView.findViewById(R.id.bookName)
        val recommendationText: TextView = itemView.findViewById(R.id.recommendationText)
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val ratingText: TextView = itemView.findViewById(R.id.ratingText)
        val bookLoadingSpinner: ProgressBar = itemView.findViewById(R.id.imageLoadingSpinner)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_book_post_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        holder.bookLoadingSpinner.visibility = View.VISIBLE
        holder.bookImage.visibility = View.GONE

        if (!book.bookImage.isNullOrEmpty()) {
            Picasso.get()
                .load(book.bookImage)
                .error(R.drawable.ic_book_placeholder)
                .into(holder.bookImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        holder.bookLoadingSpinner.visibility = View.GONE
                        holder.bookImage.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        holder.bookLoadingSpinner.visibility = View.GONE
                        holder.bookImage.visibility = View.VISIBLE
                    }
                })
        } else {
            holder.bookImage.setImageResource(R.drawable.ic_book_placeholder)
            holder.bookLoadingSpinner.visibility = View.GONE
            holder.bookImage.visibility = View.VISIBLE
        }

        holder.userName.text = book.userName
        holder.bookName.text = book.bookName
        holder.recommendationText.text = book.recommendation
        holder.ratingText.text = "${book.rating}/10"

        holder.editButton.setOnClickListener { onItemClick(book) }
    }


    override fun getItemCount() = bookList.size
}
