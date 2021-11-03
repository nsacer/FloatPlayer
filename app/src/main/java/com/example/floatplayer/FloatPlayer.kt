package com.example.floatplayer

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.NotificationCompat
import com.google.android.material.imageview.ShapeableImageView

class FloatPlayer private constructor() {

    //播放器是否活动
    private var isPlayerActive = false

    //悬浮窗是否正在显示
    private var isShowing = false
    private var mViewRoot: View? = null
    private lateinit var mViewBg: PlayerBgView
    private lateinit var mCsRoot: ConstraintLayout
    private val mCsApply = ConstraintSet()
    private val mCsReset = ConstraintSet()
    private var mContext: Context? = null

    //播放状态（默认不播放）
    private var isPlaying = false

    //控件展开状态(默认展开)
    private var isExpansion = true
    private lateinit var animCoverRotation: ObjectAnimator
    private var mediaPlayer: MediaPlayer? = null

    //音乐列表
    private val mMusicList = arrayListOf(R.raw.shanghai, R.raw.withoutyou)
    private var mMusicPosition = 0
    var mPlayControlReceiver: PlayerActionBroadCastReceiver = PlayerActionBroadCastReceiver()
    private lateinit var mNotificationManager: NotificationManager

    companion object {

        const val notificationMediaId = 10010
        const val notificationChannelMedia = "MediaNotification"

        @Volatile
        private var instance: FloatPlayer? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FloatPlayer().also { instance = it }
        }
    }

    init {

        initNotificationManager()
    }

    private fun initNotificationManager() {

        mNotificationManager = FloatApp.appContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(
                NotificationChannel(
                    notificationChannelMedia,
                    "播放器", NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    //显示播放控件
    fun show(context: Context) {
        if (!isPlayerActive) return
        mContext = context
        initMediaPlayer()
        initView()
        mViewRoot?.visibility = View.VISIBLE
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(mViewRoot, createLayoutParam(context))
        isShowing = true

        if (!isExpansion) playViewGone()
        if (isPlaying) playControlStatusSwitch(true)
    }

    fun dismiss() {
        if (!isShowing || mContext == null) return
        mViewRoot?.visibility = View.INVISIBLE
        val windowManger = mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManger.removeView(mViewRoot)
        isShowing = false
        mContext = null
    }

    //开启展示播放控件
    fun open(context: Context) {
        if (!isPlayerActive) {
            isPlayerActive = true
            show(context)
        }
    }

    //是否正在播放
    fun playing(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    //播放/暂停切换
    fun playSwitch() {
        if (mediaPlayer == null) return
        mViewRoot?.findViewById<ImageView>(R.id.ivPlayerControl)?.performClick()
    }

    //切换下一首
    fun playNext() {
        if (hasNext()) {
            mViewRoot?.findViewById<ImageView>(R.id.ivPlayerNext)?.performClick()
        } else {
            showToast("没有更多了")
        }
    }

    //关闭播放控件
    fun close() {
        if (!isPlayerActive || mContext == null) return
        destroyMediaPlayer()
        dismiss()
        isPlayerActive = false
    }

    //创建LayoutParam
    private fun createLayoutParam(context: Context): WindowManager.LayoutParams {

        val layoutParam = WindowManager.LayoutParams()
        layoutParam.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
        //弹窗层级
        layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION
        layoutParam.gravity = Gravity.START or Gravity.BOTTOM
        //背景透明
        layoutParam.format = PixelFormat.TRANSPARENT
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParam.x =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24f,
                context.resources.displayMetrics
            ).toInt()
        layoutParam.y =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80f,
                context.resources.displayMetrics
            ).toInt()
        return layoutParam
    }

    //初始化控件
    private fun initView() {

        if (mViewRoot?.tag != null) return
        mViewRoot = LayoutInflater.from(FloatApp.appContext)
            .inflate(R.layout.float_player_view, null)
        mViewRoot!!.tag = true
        mCsRoot = mViewRoot!!.findViewById(R.id.csRootFloatPlayer)
        mCsApply.clone(mCsRoot)
        mCsReset.clone(mCsRoot)

        mViewBg = mViewRoot!!.findViewById(R.id.bgViewPlayer)
        mViewRoot!!.findViewById<ShapeableImageView>(R.id.sivPlayerCover).setOnClickListener {
            playExpansionStatusSwitch(!isExpansion)
            mViewBg.doAnimation()
        }

        mViewRoot!!.findViewById<ImageView>(R.id.ivPlayerControl).setOnClickListener {
            playControlStatusSwitch(!isPlaying)
            updateNotification()
        }

        mViewRoot!!.findViewById<ImageView>(R.id.ivPlayerNext).setOnClickListener {
            mediaPlayNext()
        }
        mViewRoot!!.findViewById<ImageView>(R.id.ivPlayerClose).setOnClickListener {
            playControlStatusSwitch(false)
            cancelNotificationMedia()
            close()
        }

        initRotationAnimator(mViewRoot!!.findViewById<ImageView>(R.id.sivPlayerCover))
    }

    //初始化音频播放器
    private fun initMediaPlayer() {

        if (mediaPlayer != null) return
        mediaPlayer = MediaPlayer.create(FloatApp.appContext, mMusicList[0])
        mediaPlayer!!.setOnCompletionListener {
            mediaPlayNext()
        }
        mediaPlayer!!.setOnErrorListener { _, _, _ ->
            mediaPlayError()
            true
        }
    }

    //销毁MediaPlayer
    private fun destroyMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    //是否还有下一首
    private fun hasNext(): Boolean {
        return mMusicList.isNotEmpty() && mMusicPosition < mMusicList.size - 1
    }

    //创建音频播放器
    private fun mediaPlayNext() {

        if (!hasNext()) {
            showToast("没有更多了")
        } else {

            mediaPlayer?.stop()
            mediaPlayer?.release()

            mMusicPosition++
            mediaPlayer = MediaPlayer.create(
                FloatApp.appContext,
                mMusicList[mMusicPosition]
            )
            mediaPlayer!!.start()
            playControlStatusSwitch(true)
            updateNotification()
        }
    }

    private fun showToast(message: String) {
        if (mContext == null) return
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    //开始播放音频
    private fun mediaPlayStart() {
        if (mediaPlayer?.isPlaying == true) return
        mediaPlayer?.start()
    }

    //暂停播放音频
    private fun mediaPlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    //播放出错
    private fun mediaPlayError() {
        showToast("播放出错")
        mediaPlayer?.reset()
    }


    //播放按钮状态控制
    private fun playControlStatusSwitch(startPlay: Boolean) {

        val ivControl = mViewRoot!!.findViewById<ImageView>(R.id.ivPlayerControl)
        if (startPlay) {
            startCoverAnim()
            mediaPlayStart()
        } else {
            stopCoverAnim()
            mediaPlayPause()
        }
        ivControl.setImageResource(
            if (startPlay) R.drawable.ic_baseline_pause_24
            else R.drawable.ic_baseline_play_arrow_24
        )
        isPlaying = startPlay
    }

    //初始化旋转动画
    private fun initRotationAnimator(target: View) {
        //顺时针
        animCoverRotation = ObjectAnimator.ofFloat(target, "rotation", 0f, 360f)
        //3s一圈
        animCoverRotation.duration = 6000
        animCoverRotation.repeatMode = ValueAnimator.RESTART
        animCoverRotation.repeatCount = ValueAnimator.INFINITE
        animCoverRotation.interpolator = LinearInterpolator()
    }

    //开始播放
    private fun startCoverAnim() {

        if (animCoverRotation.isPaused) {
            animCoverRotation.resume()
        } else {
            animCoverRotation.start()
        }
    }

    //取消播放
    private fun stopCoverAnim() {
        animCoverRotation.pause()
    }

    /**
     * 封面点击切换状态
     * @param expansion 展开
     * */
    private fun playExpansionStatusSwitch(expansion: Boolean) {
        if (expansion == isExpansion) return
        if (expansion) playViewVisible() else playViewGone()
        isExpansion = expansion
    }

    //展开播放控件
    private fun playViewVisible() {
        TransitionManager.beginDelayedTransition(mCsRoot)
        mCsReset.applyTo(mCsRoot)
    }

    //收缩播放控件
    private fun playViewGone() {
        TransitionManager.beginDelayedTransition(mCsRoot)
        mCsApply.setVisibility(R.id.ivPlayerClose, View.GONE)
        mCsApply.setVisibility(R.id.ivPlayerNext, View.GONE)
        mCsApply.setVisibility(R.id.ivPlayerControl, View.GONE)
        mCsApply.applyTo(mCsRoot)
    }

    //更新播放器通知UI
    private fun updateNotification() {

        val notificationCompatAction = NotificationCompat.Action.Builder(
            if (playing()) R.drawable.ic_baseline_pause_24
            else R.drawable.ic_baseline_play_arrow_24,
            "switch",
            PendingIntent.getBroadcast(
                FloatApp.appContext,
                111,
                Intent(PlayerActionBroadCastReceiver.actionSwitch),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                else PendingIntent.FLAG_UPDATE_CURRENT
            )
        ).build()

        val nextPendingIntent = PendingIntent.getBroadcast(
            FloatApp.appContext, 222,
            Intent(PlayerActionBroadCastReceiver.actionNext),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(FloatApp.appContext, notificationChannelMedia)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(notificationCompatAction)
                .addAction(R.drawable.ic_baseline_skip_next_24, "next", nextPendingIntent)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
                .setContentTitle("这是标题")
                .setContentText("这是内容这是内容")
                .build()
        mNotificationManager.notify(notificationMediaId, notification)
    }

    //取消掉通知栏播放器
    private fun cancelNotificationMedia() {
        mNotificationManager.cancel(notificationMediaId)
    }
}