//import android.os.Bundle
//import android.text.Editable
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import com.example.ctut_student.R
//import com.example.ctut_student.data.User
//import com.example.ctut_student.databinding.EditStudentDialogBinding
//import com.example.ctut_student.viewmodel.UserManageViewModel
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.google.android.material.bottomsheet.BottomSheetDialog
//
//fun editStudentBottomSheetDialog(data: Bundle) {
//    val user = data.getParcelable<User>("user")
//    val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
//    val binding = EditStudentDialogBinding.inflate(layoutInflater)
//    val view = binding.root
//    dialog.setContentView(view)
//    dialog.behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
//    binding.apply {
//        edFirstName.text = Editable.Factory.getInstance().newEditable(user?.firstName)
//        edLastName.text = Editable.Factory.getInstance().newEditable(user?.lastName)
//        edEmail.text = Editable.Factory.getInstance().newEditable(user?.email)
//        edPhone.text = Editable.Factory.getInstance().newEditable(user?.phoneNumber)
//        edAddress.text = Editable.Factory.getInstance().newEditable(user?.address)
//        edDoB.text = Editable.Factory.getInstance().newEditable(user?.dayOfBirth.toString())
//        edSpecialty.text = Editable.Factory.getInstance().newEditable(user?.specialty)
//        val imagePath = user?.imagePath
//        if (imagePath != null) {
//            val resourceId =
//                resources.getIdentifier(imagePath, "drawable", requireContext().packageName)
//            if (resourceId != 0) {
//                ivUser.setImageResource(resourceId)
//            }
//        }
//        when (user?.gender) {
//            "male" -> genderRadioGroup.check(R.id.maleRadioButton)
//            "female" -> genderRadioGroup.check(R.id.femaleRadioButton)
//            else -> {
//                genderRadioGroup.clearCheck()
//            }
//        }
//        val gender = if (maleRadioButton.isChecked) "Male" else "Female"
//
//        val user = User(
//            edFirstName.text.toString().trim(),
//            edLastName.text.toString().trim(),
//            edEmail.text.toString().trim(),
//            role = "student",
//            edAddress.text.toString().trim(),
//            gender,
//            edPhone.text.toString().trim(),
//            edDoB.text.toString().trim(),
//            edSpecialty.text.toString().trim()
//        )
//        btnSaveEditStudent.setOnClickListener {
//            viewModel.updateUser(user,)
//        }
//    }
//    dialog.show()
//}
