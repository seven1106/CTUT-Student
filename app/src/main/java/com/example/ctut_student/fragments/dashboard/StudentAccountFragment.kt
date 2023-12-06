package com.example.ctut_student.fragments.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.FragmentAccountDetailBinding
import com.example.ctut_student.databinding.FragmentHomeBinding
import com.example.ctut_student.dialog.setupBottomSheetDialog
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClientViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StudentAccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountDetailBinding
    private val viewModel by viewModels<ClientViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    result?.data?.data?.let {
                        imageUri = it
                        Glide.with(this).load(imageUri).into(binding.ivUser)

                    }
                } else {
                    Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountDetailBinding.inflate(inflater)
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
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnEditStudent.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnEditStudent.revertAnimation()
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        binding.btnEditStudent.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
        binding.tvForgotPasswordLogin.paintFlags = binding.tvForgotPasswordLogin.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(), "Reset link was sent to your email", Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit

                }
            }
        }

        binding.btnEditStudent.setOnClickListener {
            binding.apply {
                val user = User(
                    firstName = viewModel.user.value.data?.firstName!!,
                    lastName = viewModel.user.value.data?.lastName!!,
                    email = viewModel.user.value.data?.email!!,
                    role = viewModel.user.value.data?.role!!,
                    address = edAddress.text.toString().trim(),
                    phoneNumber = edPhone.text.toString().trim(),
                    dayOfBirth = viewModel.user.value.data?.dayOfBirth!!,
                    specialty = viewModel.user.value.data?.specialty!!,
                    id = viewModel.user.value.data?.id!!,
                    userId = viewModel.user.value.data?.userId!!,
                    classId = viewModel.user.value.data?.classId!!,
                    acdermicYear = viewModel.user.value.data?.acdermicYear!!,
                    gender = viewModel.user.value.data!!.gender
                )
                viewModel.updateUser(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showUserInformation(data: User) {
        binding.apply {
            if (data.imagePath !== "") {
                Glide.with(this@StudentAccountFragment).load(data.imagePath).into(ivUser)
            }else{
            }
            edFirstName.setText(data.lastName + " " + data.firstName)
            edEmail.setText(data.email)
            edAddress.setText(data.address)
            edPhone.setText(data.phoneNumber)
            edDoB.setText(data.dayOfBirth)
            edSpecialty.setText(data.userId)
            edClassId.setText(data.classId)

        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            btnEditStudent.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            btnEditStudent.visibility = View.INVISIBLE
        }
    }

}