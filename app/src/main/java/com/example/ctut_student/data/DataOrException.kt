package com.example.ctut_student.data

data class DataOrException<T, E:Exception?>(
    val data: T? = null,
    val exception: E? = null
)
