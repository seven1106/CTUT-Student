package com.example.ctut_student.activities

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ctut_student.R
import com.example.ctut_student.data.Classroom
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.ActivityManageBinding
import com.example.ctut_student.databinding.AddStudentDialogBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.UserManageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ManageActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var btnAddStudent: LinearLayout
    private lateinit var btnAddClassroom: LinearLayout
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
        btnAddClassroom = dialogAddView.findViewById<LinearLayout>(R.id.btnAddClassroom)
        btnAddClassroom.setOnClickListener {
            showAddClassroomDialog()
        }

        dialog.show()
    }

    private fun showAddClassroomDialog() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val binding = AddStudentDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.apply {

        }
    }

    private fun showAddStudentDialog() {

        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val binding = AddStudentDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.apply {


            btnSaveEditStudent.setOnClickListener() {
                    var gender: String = ""
                    val selectedGenderId = genderRadioGroup.checkedRadioButtonId
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
                                    binding.apply {
                                        edFirstName.text.clear()
                                        edLastName.text.clear()
                                        edEmail.text.clear()
                                        edAddress.text.clear()
                                        edPhone.text.clear()
                                        edDoB.text.clear()
                                        edSpecialty.text.clear()
                                        edPassword.text.clear()
                                    }
                                    binding.btnSaveEditStudent.revertAnimation()
                                    viewModel.fetchAllUsers()

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
            dialog.show()
        }
    }



}