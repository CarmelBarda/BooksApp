package com.example.students

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class AddStudentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val idInput = view.findViewById<EditText>(R.id.idInput)
        val phoneInput = view.findViewById<EditText>(R.id.phoneInput)
        val addressInput = view.findViewById<EditText>(R.id.addressInput)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val id = idInput.text.toString()
            val phone = phoneInput.text.toString()
            val address = addressInput.text.toString()

            if (name.isNotBlank() && id.isNotBlank()) {
                MainActivity.students.add(
                    Student(id, name, false, R.drawable.ic_student_placeholder, phone, address)
                )

                showSaveSuccessDialog()
            }
        }
    }

    private fun showSaveSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage("Student details have been saved successfully.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }
}
