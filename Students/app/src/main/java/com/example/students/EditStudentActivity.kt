package com.example.students

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.students.R
import com.example.students.MainActivity

public class EditStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        val studentId = intent.getStringExtra("studentId")
        val student = MainActivity.students.find { it.id == studentId } ?: return

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val idInput = findViewById<EditText>(R.id.idInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val addressInput = findViewById<EditText>(R.id.addressInput)
        val saveButton = findViewById<Button>(R.id.saveButton)

        nameInput.setText(student.name)
        idInput.setText(student.id)
        phoneInput.setText(student.phoneNumber)
        addressInput.setText(student.address)


        saveButton.setOnClickListener {
            student.name = nameInput.text.toString()
            student.id = idInput.text.toString()
            student.phoneNumber = phoneInput.text.toString()
            student.address = addressInput.text.toString()
            finish() // Return to MainActivity
        }
    }
}
