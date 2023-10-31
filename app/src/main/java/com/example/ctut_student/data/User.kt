package com.example.ctut_student.data

import java.util.Date

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val id: Int,
    val class_id: String="",
    val role: String,
    var imagePath: String = "",
    val address: String = "",
    val gender: String = "",
    val phoneNumber: String = "",
    val dayOfBirth: Date? = null,
    val specialty: String = "",
//    val course: List<Course>? = null,

) {
    constructor() : this("", "", "", 0, "", "", "", "", "", "", null, "")
}