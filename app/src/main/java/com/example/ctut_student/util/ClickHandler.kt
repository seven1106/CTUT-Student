package com.example.ctut_student.util

class ClickHandler(
    private val onEventListener: OnEventListener
) {
    fun onClick(position: Int): Boolean {
        onEventListener.onClick(position)
        return true
    }

    fun onLongClick(position: Int): Boolean {
        onEventListener.onLongClick(position)
        return true
    }
}

interface OnEventListener {
    fun onClick(position: Int)

    fun onLongClick(position: Int)
}