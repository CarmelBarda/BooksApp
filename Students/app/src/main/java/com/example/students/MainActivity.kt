package com.example.students

import StudentAdapter
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.students.ui.theme.StudentsTheme
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object {
        val students = mutableListOf(
            Student("1", "Alice", false, R.drawable.ic_student_placeholder, "123-456-7890", "123 Main St"),
            Student("2", "Bob", true, R.drawable.ic_student_placeholder, "987-654-3210", "456 Elm St"),
            Student("3", "Charlie", false, R.drawable.ic_student_placeholder, "555-555-5555", "789 Oak St")
        )
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            title = "Students App"
        }

        recyclerView = findViewById(R.id.recyclerViewStudents)
        val fabAddStudent = findViewById<FloatingActionButton>(R.id.fabAddStudent)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter(students) { student ->
            val intent = Intent(this, StudentDetailsActivity::class.java)
            intent.putExtra("studentId", student.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        fabAddStudent.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged() // Refresh the list after returning
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StudentsTheme {
        Greeting("Android")
    }
}