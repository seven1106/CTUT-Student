package com.example.ctut_student.data

import android.os.Parcel
import android.os.Parcelable

data class Classroom(
val classId: String,
val className: String,
val description: String,
val department:String,
val adviser: String,
val adviserEmail : String,
val academicYear: String,
val schedule: Map<String, String> = emptyMap(),
val students: List<User> = emptyList()
)
    : Parcelable {
    constructor() : this("", "", "", "", "", "", "", emptyMap(), emptyList()
    )


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        return
    }
}