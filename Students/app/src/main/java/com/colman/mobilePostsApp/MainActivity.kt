package com.colman.mobilePostsApp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.colman.mobilePostsApp.data.BookPost
import com.google.firebase.FirebaseApp
import com.colman.mobilePostsApp.modules.Student

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    companion object {
        val bookPosts = mutableListOf(
            BookPost(
                userName = "Alice Johnson",
                userProfile = R.drawable.ic_student_placeholder,
                bookName = "The Great Gatsby",
                recommendation = "A beautifully written novel with deep themes of ambition and love.",
                bookImage = R.drawable.ic_book_placeholder,
                rating = 9
            ),
            BookPost(
                userName = "John Doe",
                userProfile = R.drawable.ic_student_placeholder,
                bookName = "To Kill a Mockingbird",
                recommendation = "An important novel that explores justice, empathy, and human nature.",
                bookImage = R.drawable.ic_book_placeholder,
                rating = 10
            )
        )

        val students = mutableListOf(
            Student(
                "1",
                "Alice",
                false,
                R.drawable.ic_student_placeholder,
                "123-456-7890",
                "123 Main St",
                "20/01/2002",
                "19:51"
            ),
            Student(
                "2",
                "Bob",
                true,
                R.drawable.ic_student_placeholder,
                "987-654-3210",
                "456 Elm St",
                "08/05/2000",
                "08:14"
            ),
            Student(
                "3",
                "Charlie",
                false,
                R.drawable.ic_student_placeholder,
                "555-555-5555",
                "789 Oak St",
                "13/11/2001",
                "14:48"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseInit", "Firebase initialized successfully!")
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Firebase initialization failed", e)
        }

        supportActionBar?.apply {
            title = "Book Recommendations"
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
