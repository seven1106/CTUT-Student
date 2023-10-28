package com.example.ctut_student.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ctut_student.data.User
import com.example.ctut_student.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManageViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel(){
    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
    val users: StateFlow<Resource<List<User>>> = _users

//    private val pagingInfo = PagingInfo()
    init {
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
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
}
//internal data class PagingInfo(
//    var userPage: Long = 1,
//    var oldUser: List<User> = emptyList(),
//    var isPagingEnd: Boolean = false
//)