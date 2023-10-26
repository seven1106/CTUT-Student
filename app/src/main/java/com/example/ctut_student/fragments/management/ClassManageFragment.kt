package com.example.ctut_student.fragments.management
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ctut_student.R
import com.example.ctut_student.databinding.FragmentClassManageBinding

class ClassManageFragment: Fragment(R.layout.fragment_class_manage) {
    private lateinit var binding: FragmentClassManageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassManageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}