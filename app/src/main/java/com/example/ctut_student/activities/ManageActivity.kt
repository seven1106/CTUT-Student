package com.example.ctut_student.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ctut_student.R
import com.example.ctut_student.databinding.ActivityManageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityManageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.ManageHostFragment)
        binding.bottomManageNav.setupWithNavController(navController)
    }
}