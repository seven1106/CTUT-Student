package com.example.ctut_student.fragments.management

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.ctut_student.R
import com.example.ctut_student.adapters.LessonAdapter
import com.example.ctut_student.adapters.NotiAdapter
import com.example.ctut_student.data.Lesson
import com.example.ctut_student.data.Notification
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.AddLessonDialogBinding
import com.example.ctut_student.databinding.AddNotiDialogBinding
import com.example.ctut_student.databinding.EditCourseDialogBinding
import com.example.ctut_student.databinding.FragmentCourseDetailBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.CourseManageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class CourseDetailFragment : Fragment(R.layout.fragment_classroom_detail) {

    private lateinit var binding: FragmentCourseDetailBinding
    private val args by navArgs<CourseDetailFragmentArgs>()
    private val viewModel by viewModels<CourseManageViewModel>()
    private var uri: Uri? = null
    private lateinit var pdfActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var notiAdapter: NotiAdapter
    private lateinit var lessonAdapter: LessonAdapter
    private val auth = FirebaseAuth.getInstance()
    private lateinit var emailActivityResultLauncher: ActivityResultLauncher<Intent>
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pdfActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    result?.data?.data?.let {
                        uri = it
                        Toast.makeText(requireContext(), "PDF selected", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No PDF selected", Toast.LENGTH_SHORT).show()
                }
            }
        emailActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result?.data?.data?.let {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val course = args.course
        viewModel.fetchNoti(course.classId, course.courseName)
        viewModel.fetchLesson(course.classId, course.courseName)
        setUpNotiRv()
        setUpLessonRv()

        notiAdapter.onClickDelete = {
            viewModel.deleteNoti(it)
        }
        lessonAdapter.onClickDelete = {
            viewModel.deleteLesson(it)
        }
        lessonAdapter.onClick = {
            viewModel.downloadPdf(it, requireContext())
        }
        binding.apply {
            firestore.collection("user").document(auth.uid!!)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w("TAG", "Listen failed.", error)
                        return@addSnapshotListener
                    } else {
                        val user = value?.toObject(User::class.java)
                        user?.let {
                            val userRole =
                                user.role
                            if (userRole == "student") {
                                llAddNoti.visibility = View.GONE
                                btnEditCourseInfo.visibility = View.GONE
                            } else {

                            }
                        }
                    }
                }
            btnRefresh.setOnClickListener {
                viewModel.fetchNoti(course.classId, course.courseName)
            }
            btnRefresh1.setOnClickListener {
                viewModel.fetchLesson(course.classId, course.courseName)
            }
            btnEditCourseInfo.setOnClickListener {
                showEditCourseInfoDialog()
            }
            btnContact.setOnClickListener() {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "plain/text"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(course.lecturerEmail))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Dear, ${course.lecturer}")
                emailActivityResultLauncher.launch(Intent.createChooser(intent, ""))

            }
            tvCourseName.text = course.courseName
            tvAdvisor.text = course.lecturer
            tvClassId.text = course.classId
            btnAddNoti.setOnClickListener() {
                showAddNotiDialog()
            }
            btnAddLesson.setOnClickListener() {
                showAddLessonDialog()
            }
        }
    }

    override fun onStart() {
        super.onStart()
notiAdapter.startListening()
        lessonAdapter.startListening()
    }
    private fun showEditCourseInfoDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val binding = EditCourseDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            edCourseId.setText(args.course.classId)
            edCourseName.setText(args.course.courseName)
            edLecName.setText(args.course.lecturer)
            edLEmail.setText(args.course.lecturerEmail)
            edDepartment.setText(args.course.department)
            edPTheory.setText(args.course.practicePeriods)
            edTTheory.setText(args.course.theoryPeriods)
            btneditCourse.setOnClickListener {
                val course = args.course
                course.classId = edCourseId.text.toString()
                course.courseName = edCourseName.text.toString()
                course.lecturer = edLecName.text.toString()
                course.lecturerEmail = edLEmail.text.toString()
                course.department = edDepartment.text.toString()
                course.practicePeriods = edPTheory.text.toString()
                course.theoryPeriods = edTTheory.text.toString()

                viewModel.editCourseInfo(course)
                dialog.dismiss()
            }
            }
        dialog.show()
    }

    private fun setUpLessonRv() {
         lessonAdapter = LessonAdapter()
        binding.rvLesson.apply {
            adapter = lessonAdapter
            layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(
                    requireContext(),
                    androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                    false
                )
        }
        lifecycleScope.launchWhenStarted {
            viewModel.flesson.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        lessonAdapter.differ.submitList(it.data)

                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.i("TAGclass", it.message.toString())
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUpNotiRv() {
         notiAdapter = NotiAdapter()
        binding.rvNoti.apply {
            adapter = notiAdapter
            layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(
                    requireContext(),
                    androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                    false
                )
        }
        lifecycleScope.launchWhenStarted {
            viewModel.fnoti.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        notiAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.i("TAGclass", it.message.toString())
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showAddLessonDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val binding = AddLessonDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            btnAddDoc.setOnClickListener{
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/pdf"
                pdfActivityResultLauncher.launch(intent)

            }
            btnAddLesson.setOnClickListener{
                val lesson = Lesson(
                    edTitle.text.toString(),
                    args.course.classId,
                    args.course.courseName,
                )
                viewModel.addLesson(args.course.courseName, lesson, uri!!)
            }
            lifecycleScope.launchWhenStarted {
                viewModel.lesson.collect {
                    when (it) {
                        is Resource.Loading -> {
                            btnAddLesson.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.apply {
                                edTitle.text.clear()
                            }
                            binding.btnAddLesson.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Notification added",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.fetchLesson(args.course.classId, args.course.courseName)

                        }

                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showAddNotiDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val binding = AddNotiDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {

            btnAddNoti.setOnClickListener {
                val noti = Notification(
                    edTitle.text.toString(),
                    edBody.text.toString(),
                    args.course.classId,
                    args.course.courseName,
                )
                val course = args.course
                if (noti.title.isNotEmpty() && noti.body.isNotEmpty()) {
                    viewModel.addNoti(noti)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            lifecycleScope.launchWhenStarted {
                viewModel.noti.collect {
                    when (it) {
                        is Resource.Loading -> {
                            btnAddNoti.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.apply {
                                edTitle.text.clear()
                                edBody.text.clear()

                            }
                            binding.btnAddNoti.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Notification added",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.fetchNoti(args.course.classId, args.course.courseName)
                        }

                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
        dialog.show()

    }
}