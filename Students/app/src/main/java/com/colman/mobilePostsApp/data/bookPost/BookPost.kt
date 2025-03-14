package com.colman.mobilePostsApp.data.bookPost

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.colman.mobilePostsApp.BooksApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
class BookPost(
    @PrimaryKey
    val id: String,
    val userName: String,
    var userProfile: String?,
    val bookName: String,
    val recommendation: String,
    val bookImage: String?,
    val rating: Int,
    var lastUpdated: Long? = null
) {
    companion object {
        var lastUpdated: Long
            get() {
                return BooksApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(BOOK_POST_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                BooksApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(BOOK_POST_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        const val USERNAME_KEY = "userName"
        const val USER_PROFILE_KEY = "userProfile"
        const val BOOK_NAME_KEY = "bookName"
        const val RECOMMENDATION_KEY = "recommendation"
        const val BOOK_IMAGE_KEY = "bookImage"
        const val RATING_KEY = "rating"
        const val LAST_UPDATED_KEY = "lastUpdated"
        private const val BOOK_POST_LAST_UPDATED = "bookpost_last_updated"

        fun fromJSON(json: Map<String, Any>): BookPost {
            val id = json[ID_KEY] as? String ?: ""
            val userName = json[USERNAME_KEY] as? String ?: ""
            val userProfile = json[USER_PROFILE_KEY] as? String
            val bookName = json[BOOK_NAME_KEY] as? String ?: ""
            val recommendation = json[RECOMMENDATION_KEY] as? String ?: ""
            val bookImage = json[BOOK_IMAGE_KEY] as? String
            val rating = (json[RATING_KEY] as? Long)?.toInt() ?: 0

            val bookPost = BookPost(id, userName, userProfile, bookName, recommendation, bookImage, rating)

            val lastUpdated: Timestamp? = json[LAST_UPDATED_KEY] as? Timestamp
            lastUpdated?.let {
                bookPost.lastUpdated = it.seconds
            }

            return bookPost
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                USERNAME_KEY to userName,
                USER_PROFILE_KEY to (userProfile ?: ""),
                BOOK_NAME_KEY to bookName,
                RECOMMENDATION_KEY to recommendation,
                BOOK_IMAGE_KEY to (bookImage ?: ""),
                RATING_KEY to rating,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp()
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                USERNAME_KEY to userName,
                USER_PROFILE_KEY to (userProfile ?: ""),
                BOOK_NAME_KEY to bookName,
                RECOMMENDATION_KEY to recommendation,
                BOOK_IMAGE_KEY to (bookImage ?: ""),
                RATING_KEY to rating,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp()
            )
        }
}
