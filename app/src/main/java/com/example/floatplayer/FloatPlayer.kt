package com.example.floatplayer

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.google.android.material.imageview.ShapeableImageView

class FloatPlayer private constructor(context: Context) {

    companion object {

        private lateinit var layoutParam: WindowManager.LayoutParams
        @Volatile private var instance: FloatPlayer? = null

        @Synchronized
        fun getInstance(): FloatPlayer {
            if (instance == null) instance = FloatPlayer(FloatWindowApp.getAppContext())
            return instance as FloatPlayer
        }
    }

    fun init() {

        layoutParam = WindowManager.LayoutParams()
        layoutParam.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
        //弹窗层级
        layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION
        layoutParam.gravity = Gravity.START or Gravity.BOTTOM
        //背景透明
        layoutParam.format = PixelFormat.TRANSPARENT
        //可以点击外部区域
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParam.x =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f,
                FloatWindowApp.getAppContext().resources.displayMetrics)
                .toInt()
        layoutParam.y =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f,
                FloatWindowApp.getAppContext().resources.displayMetrics)
                .toInt()

        val mViewFloat = LayoutInflater.from(FloatWindowApp.getAppContext())
            .inflate(R.layout.float_player_view, null)
        mViewFloat.findViewById<ShapeableImageView>(R.id.sivPlayerCover).setOnClickListener {
            //TODO
            Log.i("zhao", "icon点击")
        }
    }
}