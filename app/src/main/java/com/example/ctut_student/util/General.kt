package com.example.ctut_student.util


import android.content.Context
import android.os.Build
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.ctut_student.util.Constants.TAG
import com.example.ctut_student.util.Constants.SHORT_VIBRATION_DURATION


class General {
    companion object {
        fun logErrorMessage(errorMessage: String) {
            Log.d(TAG, errorMessage)
        }

        fun displayProgressBar(progressBar: ProgressBar) {
            if (progressBar.visibility == View.GONE) {
                progressBar.visibility = View.VISIBLE
            }
        }

        fun hideProgressBar(progressBar: ProgressBar) {
            if (progressBar.visibility == View.VISIBLE) {
                progressBar.visibility = View.GONE
            }
        }

        fun displayMenuItem(menuItem: MenuItem) {
            if (!menuItem.isVisible) {
                menuItem.isVisible = true
            }
        }

        fun hideMenuItem(menuItem: MenuItem) {
            if (menuItem.isVisible) {
                menuItem.isVisible = false
            }
        }

        fun vibrate(context: Context) {
            val vibrator = context.let {
                ContextCompat.getSystemService(it, Vibrator::class.java)
            }
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= 26) {
                    it.vibrate(createOneShot(SHORT_VIBRATION_DURATION, DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(SHORT_VIBRATION_DURATION)
                }
            }
        }
    }
}