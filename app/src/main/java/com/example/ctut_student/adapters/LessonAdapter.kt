package com.example.ctut_student.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ctut_student.R
import com.example.ctut_student.data.Lesson
import com.example.ctut_student.databinding.LessonRvItemBinding
import com.google.firebase.auth.FirebaseAuth

class LessonAdapter : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    inner class LessonViewHolder(private val binding: LessonRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: Lesson) {
            binding.apply {
                tvTitle.text = lesson.lessonName
                tvBody.text = "Download PDF"
                if (auth.currentUser?.email != "zxc@zxc.zxc") {
                    ibDeleteUser.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LessonRvItemBinding.inflate(inflater, parent, false)
        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = differ.currentList[position]
        holder.bind(lesson)
        holder.itemView.setOnClickListener {
            onClick?.invoke(lesson)
        }
        holder.itemView.findViewById<ImageView>(R.id.ibDeleteUser).setOnClickListener {
            onClickDelete?.invoke(lesson)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun startListening() {
        notifyDataSetChanged()

    }

    var onClick: ((Lesson) -> Unit)? = null
    var onClickDelete: ((Lesson) -> Unit)? = null

}