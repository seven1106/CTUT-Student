package com.example.ctut_student.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    var imagePath: String = "",
    val address: String = "",
    val gender: String = "",
    val phoneNumber: String = "",
    val dayOfBirth: String = "",
    val specialty: String = "",
    val scores: Map<String, String> = emptyMap(),
    val classId: String = "",
    var id: String = "",
    var userId: String = "",
    var acdermicYear: String = ""
    ) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", "", "", "", emptyMap(), "", "", "", "")


    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}