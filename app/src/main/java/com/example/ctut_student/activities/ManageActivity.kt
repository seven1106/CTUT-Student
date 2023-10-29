package com.example.ctut_student.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ctut_student.R
import com.example.ctut_student.databinding.ActivityManageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageActivity : AppCompatActivity() {
    private lateinit var dialog: BottomSheetDialog
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
        dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
        dialog.setContentView(dialogView)
        dialog.show()
    }

}