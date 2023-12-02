package com.example.ctut_student.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ctut_student.CTUTApplication
import com.example.ctut_student.data.Course
import com.example.ctut_student.data.User
import com.example.ctut_student.util.RegisterValidation
import com.example.ctut_student.util.Resource
import com.example.ctut_student.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.example.ctut_student.data.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
@HiltViewModel
class  ClientViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application

) : AndroidViewModel(app){
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _courses = MutableStateFlow<Resource<List<Course>>>(Resource.Unspecified())
    val courses: StateFlow<Resource<List<Course>>> = _courses

    private val _fnoti = MutableStateFlow<Resource<List<Notification>>>(Resource.Unspecified())
    val fnoti: StateFlow<Resource<List<Notification>>> = _fnoti

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()
    init {
        getUser()
        fetchCourse()
        fetchAllNoti()
    }
    private fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!)

            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(user))
                        }
                    }
                }
            }
    }

    fun logout(){
        auth.signOut()
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

        auth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchCourse() {
        viewModelScope.launch {
            _courses.emit(Resource.Loading())
        }

        firestore.collection("course").whereEqualTo("classId", user.value.data?.classId)
            .orderBy("courseName", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                val course = it.toObjects(Course::class.java)
                Log.i("TAGfetch", it.toString())
                viewModelScope.launch {
                    _courses.emit(Resource.Success(course))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    Log.i("TAGfetch", it.message.toString())
                    _courses.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchAllNoti() {
        viewModelScope.launch {
            _fnoti.emit(Resource.Loading())
        }
        firestore.collection("noti").whereEqualTo("classId", user.value.data?.classId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val notifications = it.toObjects(Notification::class.java)

                Log.i("TAFetch", notifications.toString())
                viewModelScope.launch {
                    _fnoti.emit(Resource.Success(notifications))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    Log.i("TAGfetch", it.message.toString())
                    _fnoti.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.address.trim().isNotEmpty()
                && user.phoneNumber.trim().isNotEmpty()
        if (!areInputsValid) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
                Toast.makeText(getApplication(), "Check your inputs", Toast.LENGTH_SHORT).show()
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }
        if (imageUri == null) {
            saveUserInformation(user, true)
        } else {
            saveUserInformationWithNewImage(user, imageUri)
        }

    }
    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<CTUTApplication>().contentResolver,
                    imageUri
                )
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory =
                    storage.child("profileImages/${user.email}/avt${auth.uid!!}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _user.emit(Resource.Error(e.message.toString()))
                    Toast.makeText(getApplication(), e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrievedOldImage: Boolean) {
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrievedOldImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(user))
                Toast.makeText(
                    getApplication(),
                    "Student account updated",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
                Log.i("fata", it.message.toString())
            }
        }
    }

    fun searchItemFirebase(searchTxt: String) {
        viewModelScope.launch { _courses.emit(Resource.Loading()) }
        firestore.collection("course")
            .whereEqualTo("courseName", searchTxt).orderBy("courseName", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                val course = it.toObjects(Course::class.java)
                viewModelScope.launch { _courses.emit(Resource.Success(course)) }

            }.addOnFailureListener{
                viewModelScope.launch { _courses.emit(Resource.Error(it.message.toString())) }
            }

    }

}
