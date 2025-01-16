package com.example.students

import StudentAdapter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.students.ui.theme.StudentsTheme
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private val students = mutableListOf<Student>() // List of students

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewStudents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Populate student list with sample data
        students.add(Student("1", "Alice", false, R.drawable.ic_student_placeholder))
        students.add(Student("2", "Bob", true, R.drawable.ic_student_placeholder))
        students.add(Student("3", "Charlie", false, R.drawable.ic_student_placeholder))

        // Initialize the adapter
        adapter = StudentAdapter(students) { student ->
            // Handle item click
            // Navigate to student details or perform an action
            println("Clicked on ${student.name}")
        }
        recyclerView.adapter = adapter
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