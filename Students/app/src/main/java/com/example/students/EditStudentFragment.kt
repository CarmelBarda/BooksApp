package com.example.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class EditStudentFragment : Fragment() {

    private var studentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the student ID from the arguments
        studentId = arguments?.getString("studentId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val student = MainActivity.students.find { it.id == studentId } ?: return

        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val idInput = view.findViewById<EditText>(R.id.idInput)
        val phoneInput = view.findViewById<EditText>(R.id.phoneInput)
        val addressInput = view.findViewById<EditText>(R.id.addressInput)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        // Populate fields with the existing student data
        nameInput.setText(student.name)
        idInput.setText(student.id)
        phoneInput.setText(student.phoneNumber)
        addressInput.setText(student.address)

        saveButton.setOnClickListener {
            student.name = nameInput.text.toString()
            student.id = idInput.text.toString()
            student.phoneNumber = phoneInput.text.toString()
            student.address = addressInput.text.toString()

            findNavController().navigateUp() // Navigate back to the previous fragment
        }

        deleteButton.setOnClickListener {
            MainActivity.students.remove(student)

            findNavController().navigateUp() // Navigate back to the previous fragment
        }

        cancelButton.setOnClickListener {
            findNavController().navigateUp() // Navigate back without making changes
        }
    }
}
