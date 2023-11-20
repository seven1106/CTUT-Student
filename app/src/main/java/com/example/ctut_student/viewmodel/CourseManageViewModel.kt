package com.example.ctut_student.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ctut_student.data.Classroom
import com.example.ctut_student.data.Course
import com.example.ctut_student.data.User
import com.example.ctut_student.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class CourseManageViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application

) : AndroidViewModel(app) {
    private val _course = MutableStateFlow<Resource<List<Course>>>(Resource.Unspecified())
    val course: StateFlow<Resource<List<Course>>> = _course


    init {
        fetchAllCourse()
    }

    fun fetchAllCourse() {
        viewModelScope.launch {
            _course.emit(Resource.Loading())
        }

        firestore.collection("course")
            .orderBy("department", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val course = it.toObjects(Course::class.java)
                Log.i("TAGfetch", it.toString())
                viewModelScope.launch {
                    _course.emit(Resource.Success(course))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    Log.i("TAGfetch", it.message.toString())
                    _course.emit(Resource.Error(it.message.toString()))
                }
            }

    }
}
