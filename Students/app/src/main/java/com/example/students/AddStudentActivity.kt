//package com.example.students
//
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import androidx.appcompat.app.AppCompatActivity
//import android.content.Intent
//import com.example.students.MainActivity
//
//
//class AddStudentActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_student)
//
//        supportActionBar?.apply {
//            title = "Student Details"
//            setDisplayHomeAsUpEnabled(true) // Show the back button
//        }
//
//        val nameInput = findViewById<EditText>(R.id.nameInput)
//        val idInput = findViewById<EditText>(R.id.idInput)
//        val phoneInput = findViewById<EditText>(R.id.phoneInput)
//        val addressInput = findViewById<EditText>(R.id.addressInput)
//        val saveButton = findViewById<Button>(R.id.saveButton)
//
//        saveButton.setOnClickListener {
//            val name = nameInput.text.toString()
//            val id = idInput.text.toString()
//            val phone = phoneInput.text.toString()
//            val address = addressInput.text.toString()
//
//            if (name.isNotBlank() && id.isNotBlank()) {
//                MainActivity.students.add(
//                    Student(id, name, false, R.drawable.ic_student_placeholder, phone, address)
//                )
//                val intent = Intent(this, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish() // Return to MainActivity
//            }
//        }
//    }
//
//    // Handle back button click
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed() // Go back to the previous activity
//        return true
//    }
//}
