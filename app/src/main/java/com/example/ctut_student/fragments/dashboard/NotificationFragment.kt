
package com.example.ctut_student.fragments.dashboard

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
import com.example.ctut_student.adapters.AllNotiAdapter
import com.example.ctut_student.adapters.NotiAdapter
import com.example.ctut_student.databinding.FragmentNotificationBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClientViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val viewModel by viewModels<ClientViewModel>()
    private lateinit var notiAdapter: AllNotiAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAllNoti()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpNotiRv()
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

    private fun setUpNotiRv() {
        notiAdapter = AllNotiAdapter()
        binding.apply {
            rvNoti.apply {
                adapter = notiAdapter
//                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            }
        }
    }

}