package com.example.ctut_student.fragments.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.example.ctut_student.adapters.client.LessonAdapter
import com.example.ctut_student.adapters.client.NotiAdapter
import com.example.ctut_student.databinding.FragmentCourseDetailClientBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.CourseManageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class CourseDetailFragmentClient : Fragment(R.layout.fragment_course_detail_client) {

    private lateinit var binding: FragmentCourseDetailClientBinding
    private val args by navArgs<CourseDetailFragmentClientArgs>()
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
        binding = FragmentCourseDetailClientBinding.inflate(inflater)
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
            btnRefresh.setOnClickListener {
                viewModel.fetchNoti(course.classId, course.courseName)
            }
            btnRefresh1.setOnClickListener {
                viewModel.fetchLesson(course.classId, course.courseName)
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
        }
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
                    }

                    else -> Unit
                }
            }
        }
    }

}