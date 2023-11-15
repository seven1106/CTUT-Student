package com.example.ctut_student.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ctut_student.data.Classroom
import com.example.ctut_student.data.User
import com.example.ctut_student.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel

class ClassroomManageViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application

) : AndroidViewModel(app) {
    private val _classrooms = MutableStateFlow<Resource<List<Classroom>>>(Resource.Unspecified())
    val classrooms: StateFlow<Resource<List<Classroom>>> = _classrooms

    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
    val users: StateFlow<Resource<List<User>>> = _users

    init {
        fetchAllClass()
        fetchStudentNoClass()
        fetchStudentInClass("")
    }

     fun fetchAllClass() {
        viewModelScope.launch {
            _classrooms.emit(Resource.Loading())
        }

            firestore.collection("classroom")
                .orderBy("classId", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    val classrooms = it.toObjects(Classroom::class.java)
                    Log.i("TAGfetch", it.toString())
                    viewModelScope.launch {
                        _classrooms.emit(Resource.Success(classrooms))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        Log.i("TAGfetch", it.message.toString())
                        _classrooms.emit(Resource.Error(it.message.toString()))
                    }
                }

    }

    fun fetchStudentNoClass() {
        viewModelScope.launch {
            _users.emit(Resource.Loading())
        }
        firestore.collection("user").whereEqualTo("classId", "").orderBy("firstName", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                val users = it.toObjects(User::class.java)
                Log.i("TAGfetch", it.toString())
                viewModelScope.launch {

                    _users.emit(Resource.Success(users))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    Log.i("TAGfetch", it.message.toString())
                    _users.emit(Resource.Error(it.message.toString()))
                }
            }

    }
    fun fetchStudentInClass(classId: String) {
        viewModelScope.launch {
            _users.emit(Resource.Loading())
        }
        firestore.collection("user").whereEqualTo("classId", classId).orderBy("firstName", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                val users = it.toObjects(User::class.java)
                Log.i("TAGfetch", it.toString())
                viewModelScope.launch {

                    _users.emit(Resource.Success(users))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    Log.i("TAGfetch", it.message.toString())
                    _users.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    fun addStudentToClass(classId: String, user: User) {
        viewModelScope.launch {
            _users.emit(Resource.Loading())
        }
        firestore.collection("classroom").document(classId).update("students", FieldValue.arrayUnion(user)).addOnSuccessListener {
        viewModelScope.launch {
                _users.emit(Resource.Success(listOf(user)))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _users.emit(Resource.Error(it.message.toString()))
            }
        }
    }
    fun updateStudentClassId(classId: String, user: User) {
        viewModelScope.launch {
            _users.emit(Resource.Loading())
        }
        firestore.collection("user").document(user.email).update("classId", classId).addOnSuccessListener {
            viewModelScope.launch {
                _users.emit(Resource.Success(listOf(user)))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _users.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}
