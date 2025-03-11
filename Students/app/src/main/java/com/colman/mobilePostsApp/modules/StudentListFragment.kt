package com.colman.mobilePostsApp.modules

import StudentAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.colman.mobilePostsApp.MainActivity
import com.colman.mobilePostsApp.R


class StudentListFragment : Fragment(R.layout.fragment_student_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewStudents)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = StudentAdapter(MainActivity.Companion.students) { student ->
            val bundle = Bundle().apply {
                putString("studentId", student.id)
            }

            findNavController().navigate(R.id.studentDetailsFragment, bundle)
        }
        recyclerView.adapter = adapter

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_student_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add_student -> {
                        findNavController().navigate(R.id.action_studentListFragment_to_addStudentFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged() // Refresh the RecyclerView when returning to the fragment
    }
}

