package com.colman.mobilePostsApp.modules

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colman.mobilePostsApp.MainActivity
import com.colman.mobilePostsApp.R
import java.util.Calendar

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
        val birthDateInput = view.findViewById<EditText>(R.id.birthDateInput)
        val birthTimeInput = view.findViewById<EditText>(R.id.birthTimeInput)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

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
            val name = nameInput.text.toString()
            val id = idInput.text.toString()
            val phone = phoneInput.text.toString()
            val address = addressInput.text.toString()
            val birthDate = birthDateInput.text.toString()
            val birthTime = birthTimeInput.text.toString()

            if (name.isNotBlank() && id.isNotBlank()) {
                MainActivity.Companion.students.add(
                    Student(
                        id, name, false, R.drawable.ic_student_placeholder,
                        phone, address, birthDate, birthTime
                    )
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
