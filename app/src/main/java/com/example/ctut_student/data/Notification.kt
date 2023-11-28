package com.example.ctut_student.data

data class Notification(
    var title: String,
    var body: String,
    val classId: String,
    val courseName: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", 0)

}

