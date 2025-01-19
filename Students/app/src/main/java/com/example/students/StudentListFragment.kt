package com.example.students

import StudentAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class StudentListFragment : Fragment(R.layout.fragment_student_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.recyclerViewStudents)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        adapter = StudentAdapter(MainActivity.students) { student ->
            // Navigate to EditStudentFragment with the selected studentId
            val bundle = Bundle().apply {
                putString("studentId", student.id)
            }

            findNavController().navigate(R.id.studentDetailsFragment, bundle)
        }

        recyclerView.adapter = adapter

        // Set up the FloatingActionButton
        val fabAddStudent = view.findViewById<FloatingActionButton>(R.id.fabAddStudent)
        fabAddStudent.setOnClickListener {
            // Navigate to AddStudentFragment
            findNavController().navigate(R.id.action_studentListFragment_to_addStudentFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged() // Refresh the RecyclerView when returning to the fragment
    }
}

