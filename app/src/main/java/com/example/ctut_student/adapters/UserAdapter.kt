package com.example.ctut_student.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ctut_student.R
import com.example.ctut_student.data.User
import com.example.ctut_student.databinding.UserRvItemBinding

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    inner class UserViewHolder(private val binding: UserRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(user: User) {
            binding.apply {
                Glide.with(itemView).load(user.imagePath).into(ivUserAvt)
                tvUserName.text = "${user.lastName} ${user.firstName}"
                tvUserClassroom.text = user.email
                tvUserId.text = user.specialty
            }

        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserRvItemBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onClick?.invoke(user)
        }

    }

    var onClick: ((User) -> Unit)? = null

}