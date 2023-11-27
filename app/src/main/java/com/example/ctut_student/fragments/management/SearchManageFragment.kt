package com.example.ctut_student.fragments.management

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ctut_student.R
import com.example.ctut_student.activities.LoginRegisterActivity
import com.example.ctut_student.databinding.FragmentNotiManageBinding
import com.example.ctut_student.viewmodel.ManageViewModel

class SearchManageFragment : Fragment(R.layout.fragment_noti_manage) {
    private lateinit var binding: FragmentNotiManageBinding
    private val uviewModel by viewModels<ManageViewModel>()
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotiManageBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sf = requireActivity().getSharedPreferences("autoLogin", Context.MODE_PRIVATE)
        editor = sf.edit()
        binding.ivLogout.setOnClickListener {
            uviewModel.logout()
            editor.clear()
            editor.apply()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}