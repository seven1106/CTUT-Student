package com.example.ctut_student.fragments.dashboard


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.FragmentHomeBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClientViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<ClientViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root

    }
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launchWhenStarted {
        viewModel.user.collectLatest {
            when (it) {
                is Resource.Loading -> {
                    showUserLoading()
                }
                is Resource.Success -> {
                    hideUserLoading()
                    showUserInformation(it.data!!)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    }

    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@HomeFragment).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(ivUser)
            tvUserName.setText(data.lastName+" "+data.firstName)

        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.INVISIBLE
            tvUserName.visibility = View.VISIBLE
            ivUser.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            tvUserName.visibility = View.INVISIBLE
            ivUser.visibility = View.INVISIBLE
        }
    }
}