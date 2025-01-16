package com.example.students

data class Student(
    var id: String,          // Student's unique ID
    var name: String,        // Student's name
    var isChecked: Boolean = false, // Checkbox status
    var image: Int           // Reference to a drawable resource
)
