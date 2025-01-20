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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        val calendar = Calendar.getInstance()

        // Parse existing birth date and pre-populate Date Picker
        if (student.birthDate.isNotBlank()) {
            birthDateInput.setText(student.birthDate) // Display value in EditText
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.parse(student.birthDate)
            if (date != null) {
                calendar.time = date
            }
        }

        birthDateInput.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    birthDateInput.setText(selectedDate)
                    calendar.set(year, month, dayOfMonth) // Update the calendar
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // comment
        // Parse existing birth time and pre-populate Time Picker
        if (student.birthTime.isNotBlank()) {
            birthTimeInput.setText(student.birthTime) // Display value in EditText
            val timeParts = student.birthTime.split(":")
            if (timeParts.size == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            }
        }

        birthTimeInput.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val selectedTime = "%02d:%02d".format(hourOfDay, minute)
                    birthTimeInput.setText(selectedTime)
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
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
