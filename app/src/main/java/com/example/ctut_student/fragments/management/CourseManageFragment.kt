package com.example.ctut_student.fragments.management

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
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
import com.example.ctut_student.activities.LoginRegisterActivity
import com.example.ctut_student.adapters.CourseAdapter
import com.example.ctut_student.databinding.FragmentCourseManageBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.CourseManageViewModel
import com.example.ctut_student.viewmodel.ManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CourseManageFragment : Fragment(R.layout.fragment_course_manage) {
    private lateinit var binding: FragmentCourseManageBinding
    private lateinit var courseAdapter: CourseAdapter
    private val viewModel by viewModels<CourseManageViewModel>()
    private val uviewModel by viewModels<ManageViewModel>()
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
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

        sf = requireActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        editor = sf.edit()
        binding.ivLogout.setOnClickListener {
            uviewModel.logout()
            editor.clear()
            editor.apply()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchTxt = s.toString().trim()
                if (searchTxt.isNotEmpty()) {
                    viewModel.searchItemFirebase(searchTxt)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.edSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val searchTxt = binding.edSearch.text.toString().trim()
                if (searchTxt.isNotEmpty()) {
                    viewModel.searchItemFirebase(searchTxt)
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.fetchAllCourse()
        }
        courseAdapter.onClick = {
            val b = Bundle().apply { putParcelable("course", it) }
            findNavController().navigate(
                R.id.action_courseManageFragment_to_courseDetailFragment,
                b
            )

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

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Update successfully", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.fetchAllCourse()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE

                        Log.e("TAGGGG", it.message.toString())

                    }

                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.delcourse.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        viewModel.fetchAllCourse()
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

    }

}