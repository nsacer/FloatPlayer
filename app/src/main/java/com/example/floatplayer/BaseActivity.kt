package com.example.floatplayer

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        FloatPlayer.getInstance().show(this)
    }

    override fun onPause() {
        super.onPause()
        FloatPlayer.getInstance().dismiss()
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}