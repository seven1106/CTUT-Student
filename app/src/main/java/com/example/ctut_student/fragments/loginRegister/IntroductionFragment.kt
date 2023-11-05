package com.example.ctut_student.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ctut_student.R
import com.example.ctut_student.databinding.FragmentIntroductionBinding

class IntroductionFragment:Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
//            findNavController().navigate(R.id.action_introductionFragment_to_loginFragment)
        }

        binding.btnRegister.setOnClickListener {
//            findNavController().navigate(R.id.action_introductionFragment_to_registerFragment)
        }
    }
}