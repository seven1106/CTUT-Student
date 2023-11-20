package com.example.ctut_student.adapters

import android.annotation.SuppressLint
import com.example.ctut_student.databinding.CourseRvItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ctut_student.data.Course

class CourseAdapter: RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {
    inner class CourseViewHolder(private val binding: CourseRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.apply {
                tvCourseName.text = course.courseName
                tvAdvisor.text = course.lecturer
                tvDepartment.text = course.classId
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem

        }

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

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Course) -> Unit)? = null

}