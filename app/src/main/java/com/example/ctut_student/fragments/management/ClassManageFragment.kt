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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ctut_student.R
import com.example.ctut_student.adapters.ClassroomAdapter
import com.example.ctut_student.adapters.UserAdapter
import com.example.ctut_student.databinding.FragmentClassManageBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClassroomManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ClassManageFragment : Fragment(R.layout.fragment_class_manage) {
    private lateinit var binding: FragmentClassManageBinding
    private lateinit var classroomAdapter: ClassroomAdapter
    private val viewModel by viewModels<ClassroomManageViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassManageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClassroomRv()
        binding.btnRefresh.setOnClickListener {
            viewModel.fetchAllClass()
        }
        classroomAdapter.onClick = {
            val b = Bundle().apply { putParcelable("classroom", it) }
            findNavController().navigate(R.id.action_classManageFragment_to_classDetailFragment, b)

        }

    }

    private fun setUpClassroomRv() {
        classroomAdapter = ClassroomAdapter()
        binding.rvClassroom.apply {
            adapter = classroomAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.classrooms.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.ClassManageProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.ClassManageProgressbar.visibility = View.GONE
                        classroomAdapter.differ.submitList(it.data)
                        Log.i("TAGclass", it.data.toString())
                    }

                    is Resource.Error -> {
                        binding.ClassManageProgressbar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }


                    else -> Unit
                }
            }
        }


    }
}