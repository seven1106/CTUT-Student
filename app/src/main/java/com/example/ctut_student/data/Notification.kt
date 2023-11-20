package com.example.ctut_student.data

data class Notification(
    var title: String,
    var body: String,
) {
    constructor() : this("", "")

}

