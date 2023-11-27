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
import com.example.ctut_student.data.Classroom
import com.example.ctut_student.data.Course
import com.example.ctut_student.data.User
import com.example.ctut_student.util.Constants
import com.example.ctut_student.util.RegisterFieldsState
import com.example.ctut_student.util.RegisterValidation
import com.example.ctut_student.util.Resource
import com.example.ctut_student.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class  UserManageViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application

) : AndroidViewModel(app){
    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
    val users: StateFlow<Resource<List<User>>> = _users

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _delUser = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val delUser = _delUser.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _createClass = MutableStateFlow<Resource<Classroom>>(Resource.Unspecified())
    val createClass: Flow<Resource<Classroom>> = _createClass

    private val _createCourse = MutableStateFlow<Resource<Course>>(Resource.Unspecified())
    val createCourse: Flow<Resource<Course>> = _createCourse

    init {
        fetchAllUsers()
        getUser()
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
    fun fetchAllUsers() {
        viewModelScope.launch {
            _users.emit(Resource.Loading())
        }
        firestore.collection("user").orderBy("firstName", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                val users = it.toObjects(User::class.java)
                viewModelScope.launch {
                    _users.emit(Resource.Success(users))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _users.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun createAccountWithEmailAndPassword(user: User, password: String, ) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()
                && user.dayOfBirth.trim().isNotEmpty()
                && user.specialty.trim().isNotEmpty()

        if (!areInputsValid) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
                Toast.makeText(getApplication(), "Check your inputs", Toast.LENGTH_SHORT).show()
            }
            return
        }
            runBlocking {
                _register.emit(Resource.Loading())
            }
            auth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { it ->
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }.addOnFailureListener {

                    _register.value = Resource.Error(it.message.toString())
                    Toast.makeText(getApplication(), it.message.toString(), Toast.LENGTH_SHORT).show()

                }

    }
    private fun saveUserInfo(userUid: String, user: User) {
        user.id = userUid
        firestore.collection(Constants.USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
                _users.value = Resource.Success(listOf(user))
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }
    fun createNewClassroom(classroomId: String, classroom: Classroom) {
        val areInputsValid = validateEmail(classroom.adviserEmail) is RegisterValidation.Success
                && classroom.className.trim().isNotEmpty()
                && classroom.adviser.trim().isNotEmpty()
                && classroom.adviserEmail.trim().isNotEmpty()
                && classroom.academicYear.trim().isNotEmpty()
                && classroom.department.trim().isNotEmpty()

        if (!areInputsValid) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
                Toast.makeText(getApplication(), "Check your inputs", Toast.LENGTH_SHORT).show()
            }
            return
        }
        viewModelScope.launch {
            _createClass.value = Resource.Loading()
        }
        firestore.collection(Constants.CLASSROOM_COLLECTION)
            .document(classroomId)
            .set(classroom)
            .addOnSuccessListener {
                _createClass.value = Resource.Success(classroom)
            }.addOnFailureListener {
                _createClass.value = Resource.Error(it.message.toString())
            }
    }


    fun createNewCourse(courseName: String, course: Course) {
        val areInputsValid = validateEmail(course.lecturerEmail) is RegisterValidation.Success
                && course.courseName.trim().isNotEmpty()
                && course.classId.trim().isNotEmpty()
                && course.lecturer.trim().isNotEmpty()
        if (!areInputsValid) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
                Toast.makeText(getApplication(), "Check your inputs", Toast.LENGTH_SHORT).show()
            }
            return
        }
        viewModelScope.launch {
            _createCourse.value = Resource.Loading()
        }
        firestore.collection(Constants.COURSE_COLLECTION)
            .document(courseName)
            .set(course)
            .addOnSuccessListener {
                Toast.makeText(
                    getApplication(),
                    "Course created",
                    Toast.LENGTH_SHORT

                )
                    .show()
                _createCourse.value = Resource.Success(course)
            }.addOnFailureListener {
                _createCourse.value = Resource.Error(it.message.toString())
            }
    }
    fun deleteUser(user: User) {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        val storageRef = storage.child("profileImages/${user.email}/avt${user.id}")
        storageRef.listAll().addOnSuccessListener {
            it.items.forEach { item ->
                item.delete()
            }
        }
        firestore.collection(Constants.USER_COLLECTION)
            .document(user.id)
            .delete()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _delUser.emit(Resource.Success(user))
                }
                Toast.makeText(getApplication(), "Delete User Success", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                viewModelScope.launch {
                    _delUser.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()
                && user.dayOfBirth.trim().isNotEmpty()
                && user.specialty.trim().isNotEmpty()

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
                    storage.child("profileImages/${user.email}/avt${user.id}")
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
            val documentRef = firestore.collection("user").document(user.id)
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
        viewModelScope.launch { _users.emit(Resource.Loading()) }
        firestore.collection("user")
            .whereEqualTo("firstName", searchTxt).orderBy("firstName", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                    val user = it.toObjects(User::class.java)
                    viewModelScope.launch { _users.emit(Resource.Success(user)) }

            }.addOnFailureListener{
                viewModelScope.launch { _users.emit(Resource.Error(it.message.toString())) }
            }

    }
}
