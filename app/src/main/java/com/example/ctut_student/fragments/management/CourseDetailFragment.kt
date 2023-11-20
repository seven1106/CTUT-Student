package com.example.ctut_student.fragments.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ctut_student.R
import com.example.ctut_student.adapters.UserAdapter
import com.example.ctut_student.databinding.FragmentCourseDetailBinding
import com.example.ctut_student.viewmodel.CourseManageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CourseDetailFragment : Fragment(R.layout.fragment_classroom_detail) {

    private lateinit var binding: FragmentCourseDetailBinding
    private val args by navArgs<CourseDetailFragmentArgs>()
    private val viewModel by viewModels<CourseManageViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseDetailBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val course = args.course
        binding.btnRefresh.setOnClickListener() {
            viewModel.fetchAllCourse()
        }
        binding.apply {
            tvCourseName.text = course.courseName
            tvAdvisor.text = course.lecturer
            tvClassId.text = course.classId

        }


    }
}