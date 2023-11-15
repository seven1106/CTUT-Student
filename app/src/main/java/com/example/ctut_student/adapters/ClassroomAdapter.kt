package com.example.ctut_student.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ctut_student.data.Classroom
import com.example.ctut_student.databinding.ClassroomRvItemBinding

class ClassroomAdapter: RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder>() {
    inner class ClassroomViewHolder(private val binding: ClassroomRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(classroom: Classroom) {
            binding.apply {
                tvClassName.text = classroom.className
                tvAdvisor.text = classroom.adviser
                tvAcademicYear.text = classroom.academicYear
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Classroom>() {
        override fun areItemsTheSame(oldItem: Classroom, newItem: Classroom): Boolean {
            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItem: Classroom, newItem: Classroom): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ClassroomRvItemBinding.inflate(inflater, parent, false)
        return ClassroomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {
        val classroom = differ.currentList[position]
        holder.bind(classroom)
        holder.itemView.setOnClickListener {
            onClick?.invoke(classroom)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Classroom) -> Unit)? = null

}