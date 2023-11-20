package com.example.ctut_student.data

data class LessonDoc(
    val lessonName: String,
    val docRef: String,
) {
    constructor() : this( "", "")

}
