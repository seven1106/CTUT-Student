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
import com.example.ctut_student.data.Course
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.ActivityManageBinding
import com.example.ctut_student.databinding.AddClassromDialogBinding
import com.example.ctut_student.databinding.AddStudentDialogBinding
import com.example.ctut_student.databinding.FragmentAddCourseBinding
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.ManageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ManageActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var btnAddStudent: LinearLayout
    private lateinit var btnAddClassroom: LinearLayout
    private lateinit var btnAddCouseL: LinearLayout
    private val viewModel by viewModels<ManageViewModel>()

    private val binding by lazy {
        ActivityManageBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = findNavController(R.id.ManageHostFragment)
        binding.bottomManageNav.setupWithNavController(navController)
        binding.btnAdd.setOnClickListener {
            onPause()
            showBtnAddDialog()
        }




    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAllUsers()
    }


    private fun showBtnAddDialog() {
        val dialogAddView = layoutInflater.inflate(R.layout.btn_add_dialog, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        dialog.setContentView(dialogAddView)
        btnAddStudent = dialogAddView.findViewById<LinearLayout>(R.id.btnAddStudent)
        btnAddStudent.setOnClickListener {
            dialog.dismiss()
            showAddStudentDialog()
        }
        btnAddClassroom = dialogAddView.findViewById<LinearLayout>(R.id.btnAddClassroom)
        btnAddClassroom.setOnClickListener {
            dialog.dismiss()
            showAddClassroomDialog()
        }
        btnAddCouseL = dialogAddView.findViewById<LinearLayout>(R.id.btnAddCourse)
        btnAddCouseL.setOnClickListener {
            dialog.dismiss()
            showAddCourseDialog()
        }

        dialog.show()
    }

    private fun showAddCourseDialog() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val binding = FragmentAddCourseBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.apply {

            btnAddCourse.setOnClickListener {
                val course = Course(
                    edCourseId.text.toString().trim(),
                    edCourseName.text.toString().trim(),
                    edLecName.text.toString().trim(),
                    edLEmail.text.toString().trim(),
                    edDepartment.text.toString().trim(),
                    edPTheory.text.toString().trim(),
                    edTTheory.text.toString().trim()
                )
                viewModel.createNewCourse(course.courseName, course)
                lifecycleScope.launchWhenStarted {
                    viewModel.createCourse.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                btnAddCourse.startAnimation()
                            }

                            is Resource.Success -> {

                                binding.apply {
                                    edCourseId.text.clear()
                                    edCourseName.text.clear()
                                    edLecName.text.clear()
                                    edLEmail.text.clear()
                                    edPTheory.text.clear()
                                    edTTheory.text.clear()
                                }
                                binding.btnAddCourse.revertAnimation()
                                dialog.dismiss()
                                val navController = findNavController(R.id.ManageHostFragment)
                                navController.navigate(R.id.courseManageFragment)
                            }

                            is Resource.Error -> {

                                binding.btnAddCourse.revertAnimation()
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

    private fun showAddClassroomDialog() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val binding = AddClassromDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.apply {

            btnCreateNewClassroom.setOnClickListener() {
                val classroom = Classroom(
                    edClassId.text.toString().trim(),
                    edClassName.text.toString().trim(),
                    edDescription.text.toString().trim(),
                    edDepartment.text.toString().trim(),
                    edAvdiser.text.toString().trim(),
                    edAvdisorEmail.text.toString().trim(),
                    edAcaYear.text.toString().trim()
                )
                viewModel.createNewClassroom(classroom.classId, classroom)
                lifecycleScope.launchWhenStarted {
                    viewModel.createClass.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                binding.btnCreateNewClassroom.startAnimation()
                            }

                            is Resource.Success -> {
                                Toast.makeText(
                                    applicationContext,
                                    "Classroom created",
                                    Toast.LENGTH_SHORT

                                )
                                    .show()
                                binding.apply {
                                    edClassId.text.clear()
                                    edClassName.text.clear()
                                    edDescription.text.clear()
                                    edDepartment.text.clear()
                                    edAvdiser.text.clear()
                                    edAvdisorEmail.text.clear()
                                    edAcaYear.text.clear()
                                }
                                binding.btnCreateNewClassroom.revertAnimation()
                                dialog.dismiss()
                                val navController = findNavController(R.id.ManageHostFragment)
                                navController.navigate(R.id.classManageFragment)
                            }

                            is Resource.Error -> {

                                binding.btnCreateNewClassroom.revertAnimation()
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
                        edSpecialty.text.toString().trim(),
                        userId = edId.text.toString().trim(),
                        acdermicYear = edAcaYear.text.toString().trim(),
                    )
                    val password = edPassword.text.toString().trim()
                    viewModel.createAccountWithEmailAndPassword(user, password)
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
                                        edId.text.clear()
                                        edAcaYear.text.clear()
                                    }
                                    binding.btnSaveEditStudent.revertAnimation()
                                }

                                is Resource.Error -> {
                                    binding.btnSaveEditStudent.revertAnimation()
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