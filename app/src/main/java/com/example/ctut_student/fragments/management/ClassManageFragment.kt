package com.example.ctut_student.fragments.management

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.ctut_student.adapters.ClassroomAdapter
import com.example.ctut_student.databinding.FragmentClassManageBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClassroomManageViewModel
import com.example.ctut_student.viewmodel.ManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ClassManageFragment : Fragment(R.layout.fragment_class_manage) {
    private lateinit var binding: FragmentClassManageBinding
    private lateinit var classroomAdapter: ClassroomAdapter
    private val viewModel by viewModels<ClassroomManageViewModel>()
    private val uviewModel by viewModels<ManageViewModel>()
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassManageBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAllClass()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClassroomRv()

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
            viewModel.fetchAllClass()
        }
        classroomAdapter.onClick = {
            val b = Bundle().apply { putParcelable("classroom", it) }
            findNavController().navigate(R.id.action_classManageFragment_to_classDetailFragment, b)

        }
        classroomAdapter.onClickDelete = {
            viewModel.deleteClass(it)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.classroom.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.ClassManageProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.ClassManageProgressbar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Delete successfully", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.fetchAllClass()
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