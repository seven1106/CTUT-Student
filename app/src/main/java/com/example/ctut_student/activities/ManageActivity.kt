package com.example.ctut_student.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.ctut_student.R
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.ActivityManageBinding
import com.example.ctut_student.databinding.AddStudentDialogBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.UserManageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

//import showAddStudentDialog

@AndroidEntryPoint
class ManageActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var btnAddStudent: LinearLayout
    private val viewModel by viewModels<UserManageViewModel>()
    private val binding by lazy {
        ActivityManageBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = findNavController(R.id.ManageHostFragment)
        binding.bottomManageNav.setupWithNavController(navController)
        binding.btnAdd.setOnClickListener {
            showBtnAddDialog()
        }




    }

    private fun showBtnAddDialog() {
        val dialogAddView = layoutInflater.inflate(R.layout.btn_add_dialog, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        dialog.setContentView(dialogAddView)
        btnAddStudent = dialogAddView.findViewById<LinearLayout>(R.id.btnAddStudent)
        btnAddStudent.setOnClickListener {
            showAddStudentDialog()
        }
        dialog.show()
    }

    private fun showAddStudentDialog() {

        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val binding = AddStudentDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.apply {


            btnSaveEditStudent.setOnClickListener() {
                if (validateInput(binding)) {

                    var gender: String = ""
                    val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
                    gender = if (selectedGenderId == R.id.maleRadioButton) {
                        "male"
                    } else {
                        "female"
                    }
                    val user = User(
                        edFirstName.text.toString().trim(),
                        edLastName.text.toString().trim(),
                        edEmail.text.toString().trim(),
                        role = "student",
                        imagePath = "",
                        edAddress.text.toString().trim(),
                        gender,
                        edPhone.text.toString().trim(),
                        edDoB.text.toString().trim(),
                        edSpecialty.text.toString().trim()
                    )
                    val password = edPassword.text.toString()
                    viewModel.createAccountWithEmailAndPassword(user, password)
                    viewModel.fetchAllUsers()


                    lifecycleScope.launchWhenStarted {
                        viewModel.register.collectLatest {
                            when (it) {
                                is Resource.Loading -> {
                                    binding.btnSaveEditStudent.startAnimation()
                                }

                                is Resource.Success -> {
                                    Toast.makeText(
                                        applicationContext,
                                        "Student account created",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    binding.btnSaveEditStudent.revertAnimation()

                                }

                                is Resource.Error -> {

                                    binding.btnSaveEditStudent.revertAnimation()
                                    Log.e("TAGGGG", it.message.toString())

                                }

                                else -> Unit
                            }
                        }
                    }
                }
            }
            dialog.show()
        }
    }

    private fun validateInput(binding: AddStudentDialogBinding): Boolean {
        with(binding) {
            if (edFirstName.text.isEmpty()) {
                edFirstName.error = "First name is required"
                edFirstName.requestFocus()
                return false
            }
            if (edLastName.text.isEmpty()) {
                edLastName.error = "Last name is required"
                edLastName.requestFocus()
                return false
            }
            if (edEmail.text.isEmpty()) {
                edEmail.error = "Email is required"
                edEmail.requestFocus()
                return false
            }
            if (edPassword.text.isEmpty()) {
                edPassword.error = "Password is required"
                edPassword.requestFocus()
                return false
            }
            if (edAddress.text.isEmpty()) {
                edAddress.error = "Address is required"
                edAddress.requestFocus()
                return false
            }
            if (edPhone.text.isEmpty()) {
                edPhone.error = "Phone is required"
                edPhone.requestFocus()
                return false
            }
            if (edDoB.text.isEmpty()) {
                edDoB.error = "Date of birth is required"
                edDoB.requestFocus()
                return false
            }
            if (edSpecialty.text.isEmpty()) {
                edSpecialty.error = "Specialty is required"
                edSpecialty.requestFocus()
                return false
            }
        }
        return true
    }

}