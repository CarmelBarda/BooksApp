package com.example.students

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.Calendar

class EditStudentFragment : Fragment() {

    private var studentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentId = arguments?.getString("studentId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val student = MainActivity.students.find { it.id == studentId } ?: return

        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val idInput = view.findViewById<EditText>(R.id.idInput)
        val phoneInput = view.findViewById<EditText>(R.id.phoneInput)
        val addressInput = view.findViewById<EditText>(R.id.addressInput)
        val birthDateInput = view.findViewById<EditText>(R.id.birthDateInput)
        val birthTimeInput = view.findViewById<EditText>(R.id.birthTimeInput)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        nameInput.setText(student.name)
        idInput.setText(student.id)
        phoneInput.setText(student.phoneNumber)
        addressInput.setText(student.address)

        // Set up Date Picker
        birthDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val date = "$dayOfMonth/${month + 1}/$year"
                    birthDateInput.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Set up Time Picker
        birthTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val time = "%02d:%02d".format(hourOfDay, minute)
                    birthTimeInput.setText(time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        saveButton.setOnClickListener {
            student.name = nameInput.text.toString()
            student.id = idInput.text.toString()
            student.phoneNumber = phoneInput.text.toString()
            student.address = addressInput.text.toString()
            student.birthDate = birthDateInput.text.toString()
            student.birthTime = birthTimeInput.text.toString()

            showSaveSuccessDialog()
        }

        deleteButton.setOnClickListener {
            MainActivity.students.remove(student)

            findNavController().navigateUp()
        }

        cancelButton.setOnClickListener {
            findNavController().navigateUp()
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
