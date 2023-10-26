package com.example.ctut_student.data

import android.net.Uri

class Course(
    val course_id: String,
    val course_name: String,
    val lecturer: String,
    val course_description: String,
    val documentRef: Uri?,
    val fee: Int,
    val course_status: String,
    val theory_periods: Int,
    val practice_periods: Int,
    val course_image: String,
    val list_student: List<String>,
    val scores: Scores

    ) {
}