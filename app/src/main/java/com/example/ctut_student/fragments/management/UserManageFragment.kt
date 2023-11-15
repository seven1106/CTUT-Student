package com.example.ctut_student.fragments.management

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ctut_student.R
import com.example.ctut_student.activities.LoginRegisterActivity
import com.example.ctut_student.adapters.UserAdapter
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.EditStudentDialogBinding
import com.example.ctut_student.databinding.FragmentUserManageBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.UserManageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val TAG = "UMFragment"

@AndroidEntryPoint
class UserManageFragment : Fragment(R.layout.fragment_user_manage) {
    private lateinit var binding: FragmentUserManageBinding
    private lateinit var userAdapter: UserAdapter
    private val viewModel by viewModels<UserManageViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sf = requireActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        editor = sf.edit()
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    result?.data?.data?.let {
                        imageUri = it
                        Glide.with(this).load(imageUri).into(binding.ivLogout)

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
        binding = FragmentUserManageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserRv()
        binding.btnRefresh.setOnClickListener {
            viewModel.fetchAllUsers()
        }
        binding.ivLogout.setOnClickListener {
            viewModel.logout()
            editor.clear()
            editor.apply()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        userAdapter.onClick = {
            val data = Bundle().apply { putParcelable("user", it) }
            editStudentBottomSheetDialog(data)
        }
        userAdapter.onClickDelete = {
            viewModel.deleteUser(it)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.delUser.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.UserManageProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.UserManageProgressbar.visibility = View.GONE
                        viewModel.fetchAllUsers()

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
        lifecycleScope.launchWhenStarted {
            viewModel.users.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.UserManageProgressbar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.UserManageProgressbar.visibility = View.GONE
                        userAdapter.differ.submitList(it.data)
                        Log.i(TAG, it.data.toString())
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

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.fetchAllUsers()
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

    fun editStudentBottomSheetDialog(data: Bundle) {
        val user = data.getParcelable<User>("user")
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        val binding = EditStudentDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        binding.apply {
            edFirstName.text = Editable.Factory.getInstance().newEditable(user?.firstName)
            edLastName.text = Editable.Factory.getInstance().newEditable(user?.lastName)
            edEmail.text = Editable.Factory.getInstance().newEditable(user?.email)
            edPhone.text = Editable.Factory.getInstance().newEditable(user?.phoneNumber)
            edAddress.text = Editable.Factory.getInstance().newEditable(user?.address)
            edDoB.text = Editable.Factory.getInstance().newEditable(user?.dayOfBirth.toString())
            edSpecialty.text = Editable.Factory.getInstance().newEditable(user?.specialty)
            if (user?.imagePath !== "") {
                Glide.with(this@UserManageFragment).load(user?.imagePath).into(ivUser)
            }
            when (user?.gender) {
                "male" -> genderRadioGroup.check(R.id.maleRadioButton)
                "female" -> genderRadioGroup.check(R.id.femaleRadioButton)
                else -> {
                    genderRadioGroup.clearCheck()
                }
            }


//            imageEdit.setOnClickListener {
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "image/*"
//                imageActivityResultLauncher.launch(intent)
//            }
            lifecycleScope.launchWhenStarted {
                viewModel.updateInfo.collect {
                    when (it) {
                        is Resource.Loading -> {
                            Log.e("TAGGGG", it.data.toString())
                            binding.btnEditStudent.startAnimation()
                        }

                        is Resource.Success -> {
                            Log.e("TAGGGG", it.data.toString())

                            binding.btnEditStudent.revertAnimation()
                            viewModel.fetchAllUsers()

                        }

                        is Resource.Error -> {
                            binding.btnEditStudent.revertAnimation()
                            Log.e("TAGGGG", it.message.toString())

                        }

                        else -> Unit
                    }
                }
            }
            btnEditStudent.setOnClickListener {
                var gender: String = ""
                val selectedGenderId = genderRadioGroup.checkedRadioButtonId
                gender = if (selectedGenderId == R.id.maleRadioButton) {
                    "male"
                } else {
                    "female"
                }
                val userEdit = User(
                    edFirstName.text.toString().trim(),
                    edLastName.text.toString().trim(),
                    email = edEmail.text.toString().trim(),
                    role = "student",
                    gender = gender,
                    address = edAddress.text.toString().trim(),
                    phoneNumber = edPhone.text.toString().trim(),
                    dayOfBirth = edDoB.text.toString().trim(),
                    specialty = edSpecialty.text.toString().trim()
                )
                viewModel.updateUser(userEdit, imageUri)

            }
        }
        dialog.show()
    }


}