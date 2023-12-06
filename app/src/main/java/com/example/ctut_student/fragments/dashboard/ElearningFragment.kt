package com.example.ctut_student.fragments.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ctut_student.R
import com.example.ctut_student.adapters.client.CourseAdapter
import com.example.ctut_student.databinding.FragmentElearningBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClientViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ElearningFragment:Fragment(R.layout.fragment_elearning){
    private lateinit var binding: FragmentElearningBinding
    private lateinit var courseAdapter: CourseAdapter
    private val viewModel by viewModels<ClientViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElearningBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCourseRv()


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
            viewModel.fetchCourse()
        }
        courseAdapter.onClick = {
            val b = Bundle().apply { putParcelable("course", it) }
            findNavController().navigate(R.id.action_elearningFragment_to_courseDetailFragmentClient, b)

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCourse()
    }

    private fun setUpCourseRv() {
        courseAdapter = CourseAdapter()
        binding.rvMyCourses.apply {
            adapter = courseAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.courses.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.Progressbar.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        binding.Progressbar.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.Progressbar.visibility = View.GONE
                        courseAdapter.differ.submitList(it.data)
                    }
                    else -> {}
                }
            }
        }
    }
}