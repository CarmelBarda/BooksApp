package com.example.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController

class StudentDetailsFragment : Fragment() {

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

        studentImage.setImageResource(student.image)
        studentName.text = "Name: ${student.name}"
        studentIdView.text = "ID: ${student.id}"
        studentPhone.text = "Phone: ${student.phoneNumber}"
        studentAddress.text = "Address: ${student.address}"

        if (student.isChecked) {
            checkIcon.visibility = View.VISIBLE
            checkedStatusText.visibility = View.VISIBLE
        } else {
            checkIcon.visibility = View.GONE
            checkedStatusText.visibility = View.GONE
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_student_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val bundle = Bundle().apply {
                    putString("studentId", student.id)
                }

                return when (menuItem.itemId) {
                    R.id.action_edit_student -> {
                        findNavController().navigate(R.id.action_studentDetailsFragment_to_editStudentFragment, bundle)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
