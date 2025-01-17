package com.example.students

data class Student(
    var id: String,
    var name: String,
    var isChecked: Boolean = false,
    var image: Int,
    var phoneNumber: String = "",
    var address: String = ""
)

