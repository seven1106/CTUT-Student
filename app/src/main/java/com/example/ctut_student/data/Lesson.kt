package com.example.ctut_student.data

data class Lesson(
    val lessonName: String,
    val classId: String,
    val courseName: String,
    var docRef: String = "",
) {
    constructor() : this("", "", "", "")

}
