package com.example.ctut_student.adapters

import android.annotation.SuppressLint
import com.example.ctut_student.databinding.CourseRvItemBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ctut_student.data.Course
import com.example.ctut_student.data.Lesson
import com.google.firebase.auth.FirebaseAuth

class CourseAdapter: RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    inner class CourseViewHolder(private val binding: CourseRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.apply {
                tvCourseName.text = course.courseName
                tvAdvisor.text = course.lecturer
                tvDepartment.text = course.classId
            }
            if (auth.currentUser?.email != "zxc@zxc.zxc") {
                binding.ibDeleteUser.visibility = View.INVISIBLE
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CourseRvItemBinding.inflate(inflater, parent, false)
        return CourseViewHolder(binding)


    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = differ.currentList[position]
        holder.bind(course)
        holder.itemView.setOnClickListener {
            onClick?.invoke(course)
        }
        holder.itemView.findViewById<android.widget.ImageView>(com.example.ctut_student.R.id.ibDeleteUser).setOnClickListener {
            onClickDelete?.invoke(course)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Course) -> Unit)? = null
    var onClickDelete: ((Course) -> Unit)? = null



}