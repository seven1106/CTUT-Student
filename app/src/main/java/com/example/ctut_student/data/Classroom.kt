package com.example.ctut_student.data

data class Classroom(
val classId: String,
val className: String,

val adviser: String,
val adviserEmail : String,
val schedule: Map<String, String>,
val students: Map<String, String>
)
