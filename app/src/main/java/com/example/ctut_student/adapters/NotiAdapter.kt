package com.example.ctut_student.adapters


import android.annotation.SuppressLint
import com.example.ctut_student.databinding.CourseRvItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ctut_student.R
import com.example.ctut_student.data.Notification
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.NotiRvItemBinding

class NotiAdapter: RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {
    inner class NotiViewHolder(private val binding: NotiRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(noti: Notification) {
            binding.apply {
                tvTitle.text = noti.title
                tvBody.text = noti.body
            }
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotiRvItemBinding.inflate(inflater, parent, false)
        return NotiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val noti = differ.currentList[position]
        holder.bind(noti)
        holder.itemView.setOnClickListener {
            onClick?.invoke(noti)
        }
        holder.itemView.findViewById<ImageView>(R.id.ibDeleteUser).setOnClickListener {
            onClickDelete?.invoke(noti)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Notification) -> Unit)? = null
    var onClickDelete: ((Notification) -> Unit)? = null
}