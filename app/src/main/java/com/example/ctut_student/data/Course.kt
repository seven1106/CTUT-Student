package com.example.ctut_student.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class Course(
    val classId: String,
    val courseName: String,
    val lecturer: String,
    val lecturerEmail: String,
    val department: String,
    val theoryPeriods: String,
        val practicePeriods: String,
    val noti: List<String> = emptyList(),
    val lesson: List<LessonDoc> = emptyList(),
    val practiceDoc: List<PracticeDoc> = emptyList(),

    ): Parcelable {
        constructor() : this( "", "", "", "", "", "", "", emptyList(), emptyList(), emptyList())

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}
