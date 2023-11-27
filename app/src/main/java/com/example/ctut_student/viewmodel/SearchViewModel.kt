package com.example.ctut_student.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class SearchViewModel@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,

    ): ViewModel() {

}