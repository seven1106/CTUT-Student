package com.example.ctut_student.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ctut_student.data.Course
import com.example.ctut_student.data.Lesson
import com.example.ctut_student.data.Notification
import com.example.ctut_student.data.User
import com.example.ctut_student.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class CourseManageViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: StorageReference,
    app: Application

) : AndroidViewModel(app) {
    private val _course = MutableStateFlow<Resource<List<Course>>>(Resource.Unspecified())
    val course: StateFlow<Resource<List<Course>>> = _course

    private val _delcourse = MutableStateFlow<Resource<Course>>(Resource.Unspecified())
    val delcourse: StateFlow<Resource<Course>> = _delcourse

    private val _updateInfo = MutableStateFlow<Resource<Course>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    private val _noti = MutableStateFlow<Resource<Notification>>(Resource.Unspecified())
    val noti: StateFlow<Resource<Notification>> = _noti

    private val _fnoti = MutableStateFlow<Resource<List<Notification>>>(Resource.Unspecified())
    val fnoti: StateFlow<Resource<List<Notification>>> = _fnoti

    private val _flesson = MutableStateFlow<Resource<List<Lesson>>>(Resource.Unspecified())
    val flesson: StateFlow<Resource<List<Lesson>>> = _flesson

    private val _lesson = MutableStateFlow<Resource<Lesson>>(Resource.Unspecified())
    val lesson: StateFlow<Resource<Lesson>> = _lesson

    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()


    init {
        fetchAllCourse()
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

    fun fetchAllCourse() {
        viewModelScope.launch {
            _course.emit(Resource.Loading())
        }

        firestore.collection("course")
            .orderBy("department", Query.Direction.DESCENDING)
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

    fun addNoti(notification: Notification) {
        viewModelScope.launch {
            _noti.emit(Resource.Loading())
        }
        firestore.collection("noti")
            .document(notification.title)
            .set(notification)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _noti.emit(Resource.Success(notification))
                }
                Toast.makeText(getApplication(), "Notification added", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                viewModelScope.launch {
                    _noti.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchNoti(classId: String, courseName: String) {
        viewModelScope.launch {
            _fnoti.emit(Resource.Loading())
        }
        firestore.collection("noti").whereEqualTo("classId", classId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereEqualTo("courseName", courseName).get()

            .addOnSuccessListener {
                val notifications = it.toObjects(Notification::class.java)
                viewModelScope.launch {
                    _fnoti.emit(Resource.Success(notifications))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _fnoti.emit(Resource.Error(it.message.toString()))

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


    fun fetchLesson(classId: String, courseName: String) {
        viewModelScope.launch {
            _flesson.emit(Resource.Loading())
        }
        firestore.collection("lesson").whereEqualTo("classId", classId)
            .whereEqualTo("courseName", courseName).get()
            .addOnSuccessListener {
                val lesson = it.toObjects(Lesson::class.java)
                viewModelScope.launch {
                    _flesson.emit(Resource.Success(lesson))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _flesson.emit(Resource.Error(it.message.toString()))

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
                        }
                        Toast.makeText(getApplication(), "Lesson added", Toast.LENGTH_SHORT)
                            .show()
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
        storageReference.delete().addOnSuccessListener {
            Toast.makeText(getApplication(), "PDF deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(getApplication(), "Deletion failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun editCourseInfo(course: Course) {
        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }
        firestore.collection("course")
            .document(course.courseName)
            .set(course)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Success(course))
                    Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            _delcourse.emit(Resource.Loading())
        }
        firestore.collection("lesson")
            .whereEqualTo("courseName", course.courseName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
        firestore.collection("noti")
            .whereEqualTo("courseName", course.courseName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
        val storageRef = storage.child("course/${course.courseName}")
        storageRef.listAll().addOnSuccessListener {
            it.items.forEach { item ->
                item.delete()
            }
        }
        firestore.collection("course")
            .whereEqualTo("courseName", course.courseName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                viewModelScope.launch {

                    _delcourse.emit(Resource.Success((course)))
                    Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _delcourse.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

    fun searchItemFirebase(searchTxt: String) {
        viewModelScope.launch { _course.emit(Resource.Loading()) }
        firestore.collection("course")
            .whereEqualTo("courseName", searchTxt).orderBy("courseName", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                val course = it.toObjects(Course::class.java)
                viewModelScope.launch { _course.emit(Resource.Success(course)) }

            }.addOnFailureListener{
                viewModelScope.launch { _course.emit(Resource.Error(it.message.toString())) }
            }

    }

}
