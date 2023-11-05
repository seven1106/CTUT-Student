package com.example.ctut_student.data

import android.os.Parcel
import android.os.Parcelable

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

    ) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", "", "", ""
    )


    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}