package com.example.ctut_student.data

import android.os.Parcel
import android.os.Parcelable

class Course(
    var classId: String,
    var courseName: String,
    var lecturer: String,
    var lecturerEmail: String,
    var department: String,
    var theoryPeriods: String,
    var practicePeriods: String,

    ): Parcelable {
        constructor() : this( "", "", "", "", "", "", "")

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(courseName)
        parcel.writeString(lecturer)
        parcel.writeString(classId)
    }
}
