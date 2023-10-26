package com.example.ctut_student.data

data class Classroom(
val classId: String,
val className: String,
val instructor: String,
val schedule: List<ClassroomSchedule>,
val students: List<User>
)
