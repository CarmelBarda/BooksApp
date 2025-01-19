package com.example.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class StudentDetailsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_student_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val student = MainActivity.students.find { it.id == studentId } ?: return

        val studentImage = view.findViewById<ImageView>(R.id.studentImage)
        val studentName = view.findViewById<TextView>(R.id.studentName)
        val studentIdView = view.findViewById<TextView>(R.id.studentId)
        val studentPhone = view.findViewById<TextView>(R.id.studentPhone)
        val studentAddress = view.findViewById<TextView>(R.id.studentAddress)
        val checkedStatusLayout = view.findViewById<LinearLayout>(R.id.checkedStatusLayout)
        val checkIcon = view.findViewById<ImageView>(R.id.checkIcon)
        val checkedStatusText = view.findViewById<TextView>(R.id.checkedStatusText)
        val editButton = view.findViewById<Button>(R.id.editButton)

        // Populate student details
        studentImage.setImageResource(student.image)
        studentName.text = "Name: ${student.name}"
        studentIdView.text = "ID: ${student.id}"
        studentPhone.text = "Phone: ${student.phoneNumber}"
        studentAddress.text = "Address: ${student.address}"

        // Show "Checked" status if the student is checked
        if (student.isChecked) {
            checkIcon.visibility = View.VISIBLE
            checkedStatusText.visibility = View.VISIBLE
        } else {
            checkIcon.visibility = View.GONE
            checkedStatusText.visibility = View.GONE
        }

        editButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("studentId", student.id)
            }
            findNavController().navigate(R.id.action_studentDetailsFragment_to_editStudentFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()

        val student = MainActivity.students.find { it.id == studentId } ?: return

        view?.apply {
            findViewById<TextView>(R.id.studentName).text = "Name: ${student.name}"
            findViewById<TextView>(R.id.studentId).text = "ID: ${student.id}"
            findViewById<TextView>(R.id.studentPhone).text = "Phone: ${student.phoneNumber}"
            findViewById<TextView>(R.id.studentAddress).text = "Address: ${student.address}"

            val checkIcon = findViewById<ImageView>(R.id.checkIcon)
            val checkedStatusText = findViewById<TextView>(R.id.checkedStatusText)
            if (student.isChecked) {
                checkIcon.visibility = View.VISIBLE
                checkedStatusText.visibility = View.VISIBLE
            } else {
                checkIcon.visibility = View.GONE
                checkedStatusText.visibility = View.GONE
            }
        }
    }
}
