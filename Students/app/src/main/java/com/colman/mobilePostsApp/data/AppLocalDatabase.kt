package com.colman.mobilePostsApp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.colman.mobilePostsApp.BooksApplication
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserDAO
import com.colman.mobilePostsApp.data.bookPost.BookPost
import com.colman.mobilePostsApp.data.bookPost.BookPostDAO

@Database(entities = [User::class, BookPost::class], version = 10, exportSchema = true)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun bookPostDao(): BookPostDAO
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {
        val context = BooksApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "books"
        ).fallbackToDestructiveMigration()
            .build()
    }
}