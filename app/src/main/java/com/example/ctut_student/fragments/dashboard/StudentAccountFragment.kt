package com.example.ctut_student.fragments.dashboard

import android.content.Intent
import android.graphics.Color
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
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ClientViewModel
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
        binding.btnEditStudent.setOnClickListener {
            binding.apply {
                val user = User(
                    edFirstName.text.toString().trim(),
                    edLastName.text.toString().trim(),
                    email = edEmail.text.toString().trim(),
                    role = "student",
                    address = edAddress.text.toString().trim(),
                    phoneNumber = edPhone.text.toString().trim(),
                    dayOfBirth = edDoB.text.toString().trim(),
                    specialty = edSpecialty.text.toString().trim(),
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

    private fun showUserInformation(data: User) {
        binding.apply {
            if (data.imagePath !== "") {
                Glide.with(this@StudentAccountFragment).load(data.imagePath).into(ivUser)
            }else{
            }
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
            edAddress.setText(data.address)
            edPhone.setText(data.phoneNumber)
            edDoB.setText(data.dayOfBirth)
            edSpecialty.setText(data.specialty)
            edClassId.setText(data.classId)


        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
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
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            btnEditStudent.visibility = View.INVISIBLE
        }
    }

}