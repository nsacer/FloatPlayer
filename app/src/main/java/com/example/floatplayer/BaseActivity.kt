package com.example.floatplayer

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
}