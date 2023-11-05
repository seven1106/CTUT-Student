package com.example.ctut_student.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ctut_student.CTUTApplication
import com.example.ctut_student.activities.ManageActivity
import com.example.ctut_student.data.User
import com.example.ctut_student.util.Constants
import com.example.ctut_student.util.RegisterFieldsState
import com.example.ctut_student.util.RegisterValidation
import com.example.ctut_student.util.Resource
import com.example.ctut_student.util.validateEmail
import com.example.ctut_student.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.internal.Contexts.getApplication
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
class UserManageViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application

) : AndroidViewModel(app){
    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
    val users: StateFlow<Resource<List<User>>> = _users

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register
    private val _validation = Channel<RegisterFieldsState> ()
    val validetion = _validation.receiveAsFlow()

    private val pagingInfo = PagingInfo()
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
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _users.emit(Resource.Loading())
            }
            firestore.collection("user")
                .orderBy("firstName", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener {
                    val users = it.toObjects(User::class.java)
                    Log.i("TAGfetch", it.toString())
                    viewModelScope.launch {
                        pagingInfo.userPage++
                        pagingInfo.oldUser = users
                        _users.emit(Resource.Success(users))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        Log.i("TAGfetch", it.message.toString())
                        _users.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                _users.emit(Resource.Success(pagingInfo.oldUser))
            }
        }
        viewModelScope.launch {
            _users.emit(Resource.Loading())
        }
        firestore.collection("user").get()
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
    fun createAccountWithEmailAndPassword(user: User, password: String, ) {

            runBlocking {
                _register.emit(Resource.Loading())
            }
            auth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }.addOnFailureListener {

                    _register.value = Resource.Error(it.message.toString())

                }

    }
    private fun saveUserInfo(userUid: String, user: User) {
        firestore.collection(Constants.USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }
    fun deleteUser(user: User) {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection(Constants.USER_COLLECTION)
            .document(user.email)
            .delete()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _user.emit(Resource.Success(user))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
//                && user.firstName.trim().isNotEmpty()
//                && user.lastName.trim().isNotEmpty()
//                && user.dayOfBirth.trim().isNotEmpty()
//                && user.specialty.trim().isNotEmpty()

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
    fun getImagesByteArray(imageUri: Uri): String? {
        var img: String? = ""
        viewModelScope.launch {
            try {
                val inputStream = ByteArrayOutputStream()
                val imgBitmap = MediaStore.Images.Media.getBitmap(
                    ManageActivity().contentResolver,
                    imageUri
                )
                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, inputStream)
                    val imageByteArray = inputStream.toByteArray()
                    val imageDirectory =
                        storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                    val result = imageDirectory.putBytes(imageByteArray).await()
                    val imageUrl = result.storage.downloadUrl.await().toString()
                    img = imageUrl

            } catch (e: Exception) {
                viewModelScope.launch {
                    _user.emit(Resource.Error(e.message.toString()))
                }
            }
        }
        return img
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
                    storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _user.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrievedOldImage: Boolean) {
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(user.email)
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
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}
internal data class PagingInfo(
    var userPage: Long = 1,
    var oldUser: List<User> = emptyList(),
    var isPagingEnd: Boolean = false
)