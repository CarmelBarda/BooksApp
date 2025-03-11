package com.colman.mobilePostsApp.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.colman.mobilePostsApp.BooksApplication
import com.colman.mobilePostsApp.data.user.User
import com.colman.mobilePostsApp.data.user.UserDAO


@Database(entities = [User::class], version = 8, exportSchema = true)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
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