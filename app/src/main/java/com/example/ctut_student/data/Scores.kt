package com.example.ctut_student.data

data class Scores(
    val id: Int,
    val studentName: String,
    val midTerm: Float,
    val endTerm: Float,
    val total: Float,
    val passed: Boolean
)
