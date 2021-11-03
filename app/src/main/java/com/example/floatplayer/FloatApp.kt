package com.example.floatplayer

import android.app.Application
import com.example.floatplayer.FloatApp
import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.content.ComponentName
import android.content.Context

/**
 * Created by Administrator on 2017/10/10.
 */
class FloatApp : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object {
        private var application: Application? = null
        @JvmStatic
        val appContext: Context
            get() = application!!.applicationContext

        /**
         * 应用是否进入后台
         * @param context
         * @return
         */
        fun isAppGoToBackground(context: Context): Boolean {
            val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.getRunningTasks(1)
            if (tasks.isNotEmpty()) {
                val topActivity = tasks[0].topActivity
                if (topActivity!!.packageName != context.packageName) {
                    return true
                }
            }
            return false
        }
    }
}