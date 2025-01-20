package com.example.students

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    companion object {
        val students = mutableListOf(
            Student("1", "Alice", false, R.drawable.ic_student_placeholder, "123-456-7890", "123 Main St"),
            Student("2", "Bob", true, R.drawable.ic_student_placeholder, "987-654-3210", "456 Elm St"),
            Student("3", "Charlie", false, R.drawable.ic_student_placeholder, "555-555-5555", "789 Oak St")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            title = "Students App"
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
