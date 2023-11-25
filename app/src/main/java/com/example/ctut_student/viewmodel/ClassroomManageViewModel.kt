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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    private val _classroom = MutableStateFlow<Resource<Classroom>>(Resource.Unspecified())
    val classroom: StateFlow<Resource<Classroom>> = _classroom

    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
    val users: StateFlow<Resource<List<User>>> = _users

    private val _upduser = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val upduser: StateFlow<Resource<User>> = _upduser

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
            _upduser.emit(Resource.Loading())
        }
        firestore.collection("user").document(user.id).update("classId", classId).addOnSuccessListener {
            viewModelScope.launch {
                _upduser.emit(Resource.Success((user)))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _upduser.emit(Resource.Error(it.message.toString()))
            }
        }
    }
    fun deleteStudentFromClass1(classId: String, user: User) {
        viewModelScope.launch {
            _users.emit(Resource.Loading())
        }
        firestore.collection("classroom").document(classId).update("students", FieldValue.arrayRemove(user)).addOnSuccessListener {
            viewModelScope.launch {
                _users.emit(Resource.Success(listOf(user)))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _users.emit(Resource.Error(it.message.toString()))
            }
        }
    }
    fun deleteStudentFromClass (user: User) {
        viewModelScope.launch {
            _upduser.emit(Resource.Loading())
        }
        firestore.collection("user").document(user.id).update("classId", "").addOnSuccessListener {
            viewModelScope.launch {
                _upduser.emit(Resource.Success((user)))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _upduser.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun deleteClass(classroom: Classroom) {
        viewModelScope.launch {
            _classrooms.emit(Resource.Loading())
        }
        firestore.collection("classroom").document(classroom.classId).delete()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _classroom.emit(Resource.Success((classroom)))
                }
            }.addOnFailureListener {
            viewModelScope.launch {
                _classroom.emit(Resource.Error(it.message.toString()))
            }
        }
        firestore.collection("user").whereEqualTo("classId", classroom.classId).get()
            .addOnSuccessListener {
                val users = it.toObjects(User::class.java)
                for (user in users) {
                    firestore.collection("user").document(user.id).update("classId", "")
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _users.emit(Resource.Success(listOf(user)))
                            }
                        }.addOnFailureListener {
                        viewModelScope.launch {
                            _users.emit(Resource.Error(it.message.toString()))
                        }
                    }
                }
            }.addOnFailureListener {
            viewModelScope.launch {
                _users.emit(Resource.Error(it.message.toString()))
            }
        }
        firestore.collection("course").whereEqualTo("classId", classroom.classId).get()
            .addOnSuccessListener {
                val courses = it.toObjects(Course::class.java)
                for (course in courses) {
                    firestore.collection("course").document(course.courseName).update("classId", "")
                }
            }
    }
}
