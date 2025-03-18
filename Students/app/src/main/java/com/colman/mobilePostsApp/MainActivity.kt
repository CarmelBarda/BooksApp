package com.colman.mobilePostsApp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.FirebaseApp
import com.colman.mobilePostsApp.modules.Student
import com.colman.mobilePostsApp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    companion object {
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseInit", "Firebase initialized successfully!")
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Firebase initialization failed", e)
        }

        supportActionBar?.apply {
            title = "Book Recommendations"
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        setupActionBarWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment,
                R.id.registerFragment -> {
                    binding.bottomNavigationView.visibility = android.view.View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = android.view.View.VISIBLE
                }
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.postsContainerFragment)
                    true
                }
                R.id.nav_add -> {
                    navController.navigate(R.id.createPostFragment)
                    true
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.userPageFragment)
                    true
                }
                R.id.nav_logout -> {
                    logoutUser()
                    true
                }
                else -> false
            }
        }

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            if (navController.currentDestination?.id == R.id.loginFragment) {
                navController.navigate(R.id.postsContainerFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()

        navController.navigate(R.id.loginFragment)
    }

    fun setBottomNavSelectedItem(itemId: Int) {
        binding.bottomNavigationView.selectedItemId = itemId
    }
}
