package com.example.floatplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FirstActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
    }

    override fun onResume() {
        super.onResume()
        FloatPlayer.getInstance().show(this)
    }

    override fun onPause() {
        super.onPause()
        FloatPlayer.getInstance().dismiss(this)
    }
}