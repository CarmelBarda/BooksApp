import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.R
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.squareup.picasso.Picasso

class BookPostAdapter(
    private var bookList: List<BookPost>
) : RecyclerView.Adapter<BookPostAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage: ImageView = itemView.findViewById(R.id.profileImage)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val bookName: TextView = itemView.findViewById(R.id.bookName)
        val recommendationText: TextView = itemView.findViewById(R.id.recommendationText)
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val ratingText: TextView = itemView.findViewById(R.id.ratingText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_book_post_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        if (!book.userProfile.isNullOrEmpty()) {
            Picasso.get()
                .load(book.userProfile)
                .placeholder(R.drawable.profile_pic_placeholder)
                .error(R.drawable.profile_pic_placeholder)
                .into(holder.userImage)
        } else {
            holder.userImage.setImageResource(R.drawable.profile_pic_placeholder)
        }

        holder.userName.text = book.userName
        holder.bookName.text = book.bookName
        holder.recommendationText.text = book.recommendation
        holder.ratingText.text = "${book.rating}/10"

        if (!book.bookImage.isNullOrEmpty()) {
            Picasso.get()
                .load(book.bookImage)
                .placeholder(R.drawable.ic_book_placeholder)
                .error(R.drawable.ic_book_placeholder)
                .into(holder.bookImage)
        } else {
            holder.bookImage.setImageResource(R.drawable.ic_book_placeholder)
        }
    }

    override fun getItemCount() = bookList.size

    fun updatePosts(newPosts: List<BookPost>) {
        bookList = newPosts
        notifyDataSetChanged()
    }
}
