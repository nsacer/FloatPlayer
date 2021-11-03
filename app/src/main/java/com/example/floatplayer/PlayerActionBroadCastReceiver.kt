package com.example.floatplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 通知栏播放控制按钮通知播放器操作
 * */
class PlayerActionBroadCastReceiver : BroadcastReceiver() {

    companion object {

        const val actionSwitch = "floatPlayer.switch"
        const val actionNext = "floatPlayer.next"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            actionSwitch -> {
                Log.i("zhao", "开始/暂停")
                FloatPlayer.getInstance().playSwitch()
            }
            actionNext -> {
                Log.i("zhao", "下一首")
                FloatPlayer.getInstance().playNext()
            }
        }
    }
}