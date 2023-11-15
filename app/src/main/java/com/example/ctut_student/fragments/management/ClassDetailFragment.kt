package com.example.ctut_student.fragments.management

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ctut_student.R
import com.example.ctut_student.adapters.UserAdapter
import com.example.ctut_student.databinding.FragmentClassroomDetailBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClassroomManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ClassDetailFragment : Fragment(R.layout.fragment_classroom_detail) {

    private lateinit var binding: FragmentClassroomDetailBinding
    private val args by navArgs<ClassDetailFragmentArgs>()
    private lateinit var userAdapter: UserAdapter
    private val viewModel by viewModels<ClassroomManageViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassroomDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val classroom = args.classroom
        viewModel.fetchStudentInClass(classroom.classId)
        setUpStudentInClassRv()
        binding.btnAddStudent.setOnClickListener() {
            val b = Bundle().apply { putParcelable("classroom", classroom) }
            findNavController().navigate(R.id.action_classDetailFragment_to_addStudentToClass, b)

        }
        binding.btnRefresh.setOnClickListener() {
            viewModel.fetchStudentInClass(classroom.classId)
        }
        binding.apply {
            tvClassName.text = classroom.className
            tvAcademicYear.text = classroom.academicYear
            tvAdvisor.text = classroom.adviser

        }

    }

    override fun onResume() {
        super.onResume()
        val classroom = args.classroom
        viewModel.fetchStudentInClass(classroom.classId)
    }
    private fun setUpStudentInClassRv() {
        userAdapter = UserAdapter()
        binding.rvUser.apply {
            adapter = userAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.users.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.UserManageProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.UserManageProgressbar.visibility = View.GONE
                        userAdapter.differ.submitList(it.data)
                        Log.i("TAGclass", it.data.toString())
                    }

                    is Resource.Error -> {
                        binding.UserManageProgressbar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.i("TAGclass", it.message.toString())
                    }


                    else -> Unit
                }
            }
        }


    }

}