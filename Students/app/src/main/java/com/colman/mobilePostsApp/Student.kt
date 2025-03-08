package com.colman.mobilePostsApp

data class Student(
    var id: String,
    var name: String,
    var isChecked: Boolean = false,
    var image: Int,
    var phoneNumber: String = "",
    var address: String = "",
    var birthDate: String = "",
    var birthTime: String = ""
)

