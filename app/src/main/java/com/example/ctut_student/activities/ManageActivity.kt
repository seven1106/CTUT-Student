package com.example.ctut_student.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ctut_student.R
import com.example.ctut_student.databinding.ActivityManageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
    private lateinit var btnAddStudent: LinearLayout
    private val binding by lazy {
        ActivityManageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.ManageHostFragment)
        binding.bottomManageNav.setupWithNavController(navController)
        binding.btnAdd.setOnClickListener{
            showBtnAddDialog()
        }
    }

    private fun showBtnAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.btn_add_dialog, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        dialog.setContentView(dialogView)
        btnAddStudent = dialogView.findViewById<LinearLayout>(R.id.btnAddStudent)
        btnAddStudent.setOnClickListener {
            showAddStudentDialog()
        }
        dialog.show()
    }

    private fun showAddStudentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.add_student_dialog, null)
        dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        dialog.setContentView(dialogView)
        dialog.show()
    }

}