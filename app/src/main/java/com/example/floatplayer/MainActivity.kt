package com.example.floatplayer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.floatplayer.databinding.ActivityMainBinding
import com.example.floatplayer.floatWindow_third.ContactWindowUtil


open class MainActivity : BaseActivity() {

    private val mLauncherPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                toast("有系统弹窗权限")
            } else {
                toast(R.string.please_grant_permission)
            }
        }
    private val mLauncherOverlay = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        toast(if (it.resultCode == RESULT_OK) "数据OK" else "数据异常")
    }

    private lateinit var bindingMain: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
        initView()
    }

    override fun onStart() {
        super.onStart()
        registerPlayControlReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelPlayControlReceiver()
        FloatPlayer.getInstance().close()
    }

    private fun initView() {

        bindingMain.btnNewPage.setOnClickListener {
            startActivity(Intent(this, FirstActivity::class.java))
        }

        bindingMain.btnOpenPlayer.setOnClickListener {
            FloatPlayer.getInstance().open(this)
//            FloatPlayer.getInstance().appOpen()
        }

        bindingMain.btnStartService.setOnClickListener {
            toast("启动服务")
            startJobService()
        }

        // 错误打印
        bindingMain.btnPrintError.setOnClickListener {
            printError()
        }
    }

    private fun printError() {

        val one = arrayOf("111", "222", "333")
        try {
            showMessage(one[4].plus("0"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        showMessage("after Exception")
    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                toast("有系统弹窗权限")
            } else {
                requestOverlayPermission()
            }
        } else {
            toast("系统版本低于23")
        }
        mLauncherPermission.launch(Manifest.permission.SYSTEM_ALERT_WINDOW)
    }

    //请求悬浮窗权限
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestOverlayPermission() {

        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        mLauncherOverlay.launch(intent)
    }

    //创建悬浮窗
    private fun initContactDialog() {

        val mContactUtil =
            ContactWindowUtil(this)
        mContactUtil.setDialogListener {
            toast(it)
        }
        mContactUtil.showContactView()
    }

    private fun toast(resId: Int) {
        toast(getString(resId))
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    //创建通知
    private fun createNotification() {

        val notificationCompatAction = NotificationCompat.Action.Builder(
            if (FloatPlayer.getInstance().playing()) R.drawable.ic_baseline_pause_24
            else R.drawable.ic_baseline_play_arrow_24,
            "switch",
            PendingIntent.getBroadcast(
                this,
                111,
                Intent(PlayerActionBroadCastReceiver.actionSwitch),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                else PendingIntent.FLAG_UPDATE_CURRENT
            )
        ).build()

        val nextPendingIntent = PendingIntent.getBroadcast(
            this, 222,
            Intent(PlayerActionBroadCastReceiver.actionNext),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(
            this,
            FloatPlayer.notificationChannelMedia
        )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(notificationCompatAction)
            .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setContentTitle("这是标题")
            .setContentText("这是内容这是内容")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_player_cover))
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    FloatPlayer.notificationChannelMedia,
                    "播放器", NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        notificationManager.notify(FloatPlayer.notificationMediaId, notification)
    }

    //启动JobService
    private fun startJobService() {

        JobInfo.Builder(
            PlayerJobService.ID_JOB_PLAYER,
            ComponentName(this, PlayerJobService::class.java)
        ).apply {
            setOverrideDeadline(PlayerJobService.TIME_DELAY_START_JOB)
            setPeriodic(1000L)
        }.let {
            (getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(it.build())
        }
    }

    //注册控制接受receiver
    private fun registerPlayControlReceiver() {

        registerReceiver(
            FloatPlayer.getInstance().mPlayControlReceiver,
            IntentFilter().apply {
                addAction(PlayerActionBroadCastReceiver.actionNext)
                addAction(PlayerActionBroadCastReceiver.actionSwitch)
            }
        )
    }

    //取消注册receiver
    private fun cancelPlayControlReceiver() {
        unregisterReceiver(FloatPlayer.getInstance().mPlayControlReceiver)
    }

}