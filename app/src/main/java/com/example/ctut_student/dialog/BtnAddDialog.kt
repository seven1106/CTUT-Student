package com.example.ctut_student.dialog

import android.annotation.SuppressLint
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.ctut_student.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("InflateParams")
fun Fragment.showBtnAddDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.btn_add_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val btnAddStudent = view.findViewById<Button>(R.id.btnAddStudent)
    val btnAddClassroom = view.findViewById<Button>(R.id.btnAddClassroom)
    val btnAddCourse = view.findViewById<Button>(R.id.btnAddCourse)

    btnAddStudent.setOnClickListener {
        onSendClick("Student")
        dialog.dismiss()
    }

    btnAddClassroom.setOnClickListener {
        onSendClick("Student")
        dialog.dismiss()
    }
    btnAddCourse.setOnClickListener {
        onSendClick("Student")
        dialog.dismiss()
    }
}