package com.example.floatplayer

import android.content.Context
import android.view.WindowManager

class FloatPlayer private constructor(context: Context) {

    companion object {

        private lateinit var mLayoutParam: WindowManager.LayoutParams
        @Volatile private var instance: FloatPlayer? = null

        @Synchronized
        fun getInstance(): FloatPlayer {
            if (instance == null) instance = FloatPlayer(FloatWindowApp.getAppContext())
            return instance as FloatPlayer
        }
    }

    fun init() {

        mLayoutParam = WindowManager.LayoutParams()
    }
}