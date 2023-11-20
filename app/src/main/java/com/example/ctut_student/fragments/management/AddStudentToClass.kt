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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ctut_student.R
import com.example.ctut_student.adapters.UserAdapter
import com.example.ctut_student.databinding.FragmentAddStToClBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClassroomManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddStudentToClass : Fragment(R.layout.fragment_add_st_to_cl) {

    private lateinit var binding: FragmentAddStToClBinding
    private lateinit var userAdapter: UserAdapter
    private val viewModel by viewModels<ClassroomManageViewModel>()
    private val args by navArgs<AddStudentToClassArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStToClBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpStudentNoClassRv()
        val classroom = args.classroom
        userAdapter.onClick = {
            viewModel.updateStudentClassId(classroom.classId, it)
        }

        binding.apply {
            btnRefresh.setOnClickListener() {
                viewModel.fetchStudentNoClass()
            }
        }

    }

    private fun setUpStudentNoClassRv() {
        userAdapter = UserAdapter()
        binding.rvUserNoClass.apply {
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
        lifecycleScope.launchWhenStarted {
            viewModel.upduser.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.UserManageProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.UserManageProgressbar.visibility = View.GONE
                        Log.i("TAGclass", it.data.toString())
                        viewModel.fetchStudentNoClass()
                        Toast.makeText(requireContext(), "Add student successfully", Toast.LENGTH_SHORT).show()
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