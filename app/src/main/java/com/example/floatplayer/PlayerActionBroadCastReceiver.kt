package com.example.floatplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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
                FloatPlayer.getInstance().playSwitch()
            }
            actionNext -> {
                FloatPlayer.getInstance().playNext()
            }
        }
    }
}