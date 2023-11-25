package com.example.ctut_student.fragments.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ctut_student.R
import com.example.ctut_student.adapters.CourseAdapter
import com.example.ctut_student.databinding.FragmentCourseManageBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.CourseManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class CourseManageFragment : Fragment(R.layout.fragment_course_manage) {
    private lateinit var binding: FragmentCourseManageBinding
    private lateinit var courseAdapter: CourseAdapter
    private val viewModel by viewModels<CourseManageViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseManageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCourseRv()
        binding.btnRefresh.setOnClickListener {
            viewModel.fetchAllCourse()
        }
        courseAdapter.onClick = {
            val b = Bundle().apply { putParcelable("course", it) }
            findNavController().navigate(R.id.action_courseManageFragment_to_courseDetailFragment, b)

        }
        courseAdapter.onClickDelete = {
            viewModel.deleteCourse(it)
        }
    }

    private fun setUpCourseRv() {
        courseAdapter = CourseAdapter()
        binding.rvCourse.apply {
            adapter = courseAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.course.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        courseAdapter.differ.submitList(it.data)
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