package com.example.floatplayer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.floatplayer.floatWindow_third.ContactWindowUtil
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    override fun onStart() {
        super.onStart()
        registerPlayControlReceiver()
    }

    override fun onResume() {
        super.onResume()
        FloatPlayer.getInstance().show(this@MainActivity)
    }

    override fun onPause() {
        super.onPause()
        FloatPlayer.getInstance().dismiss(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelPlayControlReceiver()
        FloatPlayer.getInstance().close()
    }

    private fun initView() {

        btnNewPage.setOnClickListener {
            startActivity(Intent(this, FirstActivity::class.java))
        }

        btnOpenPlayer.setOnClickListener {
            FloatPlayer.getInstance().open(this)
        }

        btnStartService.setOnClickListener {
            toast("启动服务")
            startJobService()
        }
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
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        ).build()

        val nextPendingIntent = PendingIntent.getBroadcast(
            this, 222,
            Intent(PlayerActionBroadCastReceiver.actionNext), PendingIntent.FLAG_UPDATE_CURRENT
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

        val builder = JobInfo.Builder(0, ComponentName(this, PlayerJobService::class.java))
        // 设置启动后多长时间范围内随机开始执行任务
        builder.setOverrideDeadline(0L)
        (getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
    }

    //注册控制接受receiver
    private fun registerPlayControlReceiver() {

        val intentFilter = IntentFilter()
        intentFilter.addAction(PlayerActionBroadCastReceiver.actionSwitch)
        intentFilter.addAction(PlayerActionBroadCastReceiver.actionNext)
        registerReceiver(FloatPlayer.getInstance().mPlayControlReceiver, intentFilter)
    }

    //取消注册receiver
    private fun cancelPlayControlReceiver() {
        unregisterReceiver(FloatPlayer.getInstance().mPlayControlReceiver)
    }

}