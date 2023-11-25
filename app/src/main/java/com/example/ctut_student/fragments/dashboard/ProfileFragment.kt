package com.example.ctut_student.fragments.dashboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import br.com.simplepass.loadingbutton.BuildConfig
import com.bumptech.glide.Glide
import com.example.ctut_student.activities.LoginRegisterActivity
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.FragmentProfileBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClientViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ProfileFragment:Fragment(){
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ClientViewModel>()
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sf = requireActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        editor = sf.edit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"
            btnLogout.setOnClickListener {
                viewModel.logout()
                editor.clear()
                editor.apply()
                val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
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
            Glide.with(this@ProfileFragment).load(data.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            tvUserName.setText(data.lastName+" "+data.firstName)

        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarSettings.visibility = View.INVISIBLE
            tvUserName.visibility = View.VISIBLE
            imageUser.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarSettings.visibility = View.VISIBLE
            tvUserName.visibility = View.INVISIBLE
            imageUser.visibility = View.INVISIBLE
        }
    }
}