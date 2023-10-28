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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ctut_student.R
import com.example.ctut_student.adapters.UserAdapter
import com.example.ctut_student.databinding.FragmentUserManageBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.UserManageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "UMFragment"
@AndroidEntryPoint
class UserManageFragment : Fragment(R.layout.fragment_user_manage) {
    private lateinit var binding: FragmentUserManageBinding
    private lateinit var userAdapter: UserAdapter
    private val viewModel by viewModels<UserManageViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserManageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserRv()
        lifecycleScope.launchWhenStarted {
            viewModel.users.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.UserManageProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.UserManageProgressbar.visibility = View.GONE
                        userAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.UserManageProgressbar.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

    }

    private fun setupUserRv() {
        userAdapter = UserAdapter()
        binding.rvUser.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = userAdapter
        }
    }


}