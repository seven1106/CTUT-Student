package com.example.ctut_student.fragments.loginRegister

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ctut_student.R
import com.example.ctut_student.activities.DashboardActivity
import com.example.ctut_student.activities.ManageActivity
import com.example.ctut_student.databinding.FragmentLoginBinding
import com.example.ctut_student.dialog.setupBottomSheetDialog
import com.example.ctut_student.util.RegisterValidation
import com.example.ctut_student.util.Resource
import com.example.ctut_student.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sf = requireActivity().getSharedPreferences("autoLogin", MODE_PRIVATE)
        editor = sf.edit()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.tvForgotPasswordLogin.paintFlags = binding.tvForgotPasswordLogin.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.apply {
            tvDontHaveAccount.paintFlags = binding.tvDontHaveAccount.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val emailsf = sf.getString("email", "")
        val passwordsf = sf.getString("password", "")
        val isChecked = sf.getBoolean("isChecked", false)
        if (isChecked) {
            buttonLoginLogin.startAnimation()
            edEmailLogin.setText(emailsf)
            edPasswordLogin.setText(passwordsf)
            cbRememberMeLogin.isChecked = true
            if (emailsf != null && passwordsf != null) {
                viewModel.login(emailsf, passwordsf)
            }
        }
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)

                if (cbRememberMeLogin.isChecked) {
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.putBoolean("isChecked", true)
                    editor.apply()
                } else {
                    editor.clear()
                    editor.apply()
                }

            }
        }


        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validetion.collect { validation ->
                if (validation.email is RegisterValidation.Failed)
                    withContext(Dispatchers.Main) {
                        binding.edEmailLogin.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }

                if (validation.password is RegisterValidation.Failed)
                    withContext(Dispatchers.Main) {
                        binding.edPasswordLogin.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }

            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        firebaseAuth = FirebaseAuth.getInstance()
                        firestore = FirebaseFirestore.getInstance()
                        firebaseUser = firebaseAuth.currentUser!!
                        editor.putString("uid", it.data!!.uid)
                        editor.apply()
                        checkUserRole(it.data.uid)
                        binding.buttonLoginLogin.revertAnimation()

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }

                    else -> Unit

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(), "Reset link was sent to your email", Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> Unit

                }
            }
        }

    }

    private fun checkUserRole(uid: String) {
        firestore.collection("user").document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val role = documentSnapshot.getString("role")
                if (role == "student") {
                    Intent(requireActivity(), DashboardActivity::class.java).also { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } else if (role == "admin") {
                    Intent(requireActivity(), ManageActivity::class.java).also { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                    }
                }
            }
    }

}
