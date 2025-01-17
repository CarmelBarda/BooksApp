package com.example.students

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.students.R
import com.example.students.MainActivity

class StudentDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        supportActionBar?.apply {
            title = "Student Details"
            setDisplayHomeAsUpEnabled(true) // Show the back button
        }

        val studentId = intent.getStringExtra("studentId")
        val student = MainActivity.students.find { it.id == studentId } ?: return

        val studentImage = findViewById<ImageView>(R.id.studentImage)
        val studentName = findViewById<TextView>(R.id.studentName)
        val studentIdView = findViewById<TextView>(R.id.studentId)
        val studentPhone = findViewById<TextView>(R.id.studentPhone)
        val studentAddress = findViewById<TextView>(R.id.studentAddress)
        val editButton = findViewById<Button>(R.id.editButton)

        studentImage.setImageResource(student.image)
        studentName.text = student.name
        studentIdView.text = student.id
        studentPhone.text = student.phoneNumber
        studentAddress.text = student.address

        editButton.setOnClickListener {
            val intent = Intent(this, EditStudentActivity::class.java)
            intent.putExtra("studentId", student.id)
            startActivity(intent)
        }
    }

    // Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Go back to the previous activity
        return true
    }
}
