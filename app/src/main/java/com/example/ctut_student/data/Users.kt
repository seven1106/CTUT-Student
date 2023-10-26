package com.example.ctut_student.data

import java.util.Date

sealed class Users{
    abstract val firstName: String
    abstract val lastName: String
    abstract val email: String
    abstract val role: String
    abstract val imagePath: String
    abstract val address: String
    abstract val gender: String
    abstract val phoneNumber: String
    abstract val dayOfBirth: Date?
    data class Student(
        override val firstName: String,
        override val lastName: String,
        override val email: String,
        override val role: String,
        override val imagePath: String,
        override val address: String,
        override val phoneNumber: String,
        override val dayOfBirth: Date?,
        override val gender: String,
        val classId: String,
        val specialty : String,
        val course : List<String>,
    ): Users() {
    }    data class Lecturer(
        override val firstName: String,
        override val lastName: String,
        override val email: String,
        override val role: String,
        override val imagePath: String,
        override val address: String,
        override val phoneNumber: String,
        override val dayOfBirth: Date?,
        override val gender: String,
        val classId: String,
        val specialty : String,
        val course : List<String>,
    ): Users() {
    }

}
