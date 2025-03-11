import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.data.BookPost
import com.colman.mobilePostsApp.R

class BookPostAdapter(
    private val bookList: List<BookPost>
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

        holder.userImage.setImageResource(book.userProfile)
        holder.userName.text = book.userName
        holder.bookName.text = book.bookName
        holder.recommendationText.text = book.recommendation
        holder.bookImage.setImageResource(book.bookImage)
        holder.ratingText.text = "${book.rating}/10"
    }

    override fun getItemCount() = bookList.size
}
