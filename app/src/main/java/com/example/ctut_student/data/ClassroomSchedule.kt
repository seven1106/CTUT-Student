package com.example.ctut_student.data

data class ClassroomSchedule(
    val scheduleId: Int,
    val subject: String,
    val location: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val lecturer: String,
    val description: String?
)
