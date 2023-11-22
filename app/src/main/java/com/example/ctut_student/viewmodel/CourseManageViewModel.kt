package com.example.ctut_student.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ctut_student.CTUTApplication
import com.example.ctut_student.data.Course
import com.example.ctut_student.data.Lesson
import com.example.ctut_student.data.Notification
import com.example.ctut_student.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
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

    private val _noti = MutableStateFlow<Resource<Notification>>(Resource.Unspecified())
    val noti: StateFlow<Resource<Notification>> = _noti

    private val _fnoti = MutableStateFlow<Resource<List<Notification>>>(Resource.Unspecified())
    val fnoti: StateFlow<Resource<List<Notification>>> = _fnoti

    private val _flesson = MutableStateFlow<Resource<List<Lesson>>>(Resource.Unspecified())
    val flesson: StateFlow<Resource<List<Lesson>>> = _flesson

    private val _lesson = MutableStateFlow<Resource<Lesson>>(Resource.Unspecified())
    val lesson: StateFlow<Resource<Lesson>> = _lesson


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

    fun addNoti(courseName: String, notification: Notification) {
        viewModelScope.launch {
            _noti.emit(Resource.Loading())
        }
        firestore.collection("noti")
            .document()
            .set(notification)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _noti.emit(Resource.Success(notification))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _noti.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun featchNoti(classId: String, courseName: String) {
        viewModelScope.launch {
            _fnoti.emit(Resource.Loading())
        }
        firestore.collection("noti").whereEqualTo("classId", classId)
            .whereEqualTo("courseName", courseName).get()
            .addOnSuccessListener {
                val notifications = it.toObjects(Notification::class.java)
                viewModelScope.launch {
                    _fnoti.emit(Resource.Success(notifications))
                    Log.i("TAGnoti", notifications.toString())
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _fnoti.emit(Resource.Error(it.message.toString()))
                    Log.i("TAGnotif", it.message.toString())

                }
            }
    }

    fun deleteNoti(noti: Notification) {
        viewModelScope.launch {
            _noti.emit(Resource.Loading())
        }

        firestore.collection("noti")
            .whereEqualTo("title", noti.title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                viewModelScope.launch {
                    _noti.emit(Resource.Success(noti))
                    Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _noti.emit(Resource.Error(exception.message.toString()))
                }
            }
    }


    fun featchLesson(classId: String, courseName: String) {
        viewModelScope.launch {
            _flesson.emit(Resource.Loading())
        }
        firestore.collection("lesson").whereEqualTo("classId", classId)
            .whereEqualTo("courseName", courseName).get()
            .addOnSuccessListener {
                val lesson = it.toObjects(Lesson::class.java)
                viewModelScope.launch {
                    _flesson.emit(Resource.Success(lesson))
                    Log.i("TAGnoti", lesson.toString())
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _flesson.emit(Resource.Error(it.message.toString()))
                    Log.i("TAGnotif", it.message.toString())

                }
            }
    }

    fun addLesson(courseName: String, lesson: Lesson, uri: Uri) {
        viewModelScope.launch {
            _lesson.emit(Resource.Loading())
        }
        val mRef = storage.child("course/$courseName/${lesson.lessonName}")
        mRef.putFile(uri).addOnSuccessListener {
            mRef.downloadUrl.addOnSuccessListener {
                lesson.docRef = it.toString()
                firestore.collection("lesson")
                    .document()
                    .set(lesson)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            _lesson.emit(Resource.Success(lesson))
                            Toast.makeText(getApplication(), "Lesson added", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }.addOnFailureListener {
                        viewModelScope.launch {
                            _lesson.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }
        }

    }
    fun downloadPdf(lesson: Lesson, context: Context) {
        viewModelScope.launch {
            _lesson.emit(Resource.Loading())
        }

        val downloadUrl = lesson.docRef
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl)

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val request = DownloadManager.Request(uri)
                .setTitle("${lesson.lessonName}.pdf")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${lesson.lessonName}.pdf")
            Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
            downloadManager.enqueue(request)
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Download failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


    fun deleteLesson(lesson: Lesson) {
        viewModelScope.launch {
            _lesson.emit(Resource.Loading())
        }
        val storageReference = FirebaseStorage.getInstance().getReference("course/$lesson.courseName/$lesson.lessonName.pdf")

        storageReference.delete().addOnSuccessListener {
            // File deleted successfully
            Toast.makeText(getApplication(), "PDF deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // Handle any errors that may occur during the deletion process
            Toast.makeText(getApplication(), "Deletion failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
        firestore.collection("lesson")
            .whereEqualTo("lessonName", lesson.lessonName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                viewModelScope.launch {
                    _lesson.emit(Resource.Success(lesson))
                    Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _lesson.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

    fun editCourseInfo(course: Course) {
        viewModelScope.launch {
            _course.emit(Resource.Loading())
        }
        firestore.collection("course")
            .document(course.courseName)
            .set(course)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _course.emit(Resource.Success(listOf(course)))
                    Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _course.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}
