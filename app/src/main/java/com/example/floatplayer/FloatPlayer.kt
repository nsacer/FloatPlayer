package com.example.floatplayer

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
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
import com.google.android.material.imageview.ShapeableImageView

class FloatPlayer private constructor() {

    //是否关闭控制器
    private var isHideFloatPlayer = false

    //视图是否初始化
    private var isViewInit = false

    //悬浮窗是否正在显示
    private var isShowing = false
    private lateinit var layoutParam: WindowManager.LayoutParams
    private lateinit var mViewRoot: View
    private lateinit var mCsRoot: ConstraintLayout
    private val mCsApply = ConstraintSet()
    private val mCsReset = ConstraintSet()
    private var mContext: Context? = null

    //播放状态（默认不播放）
    private var isPlaying = false

    //控件展开状态(默认展开)
    private var isExpansion = true
    private lateinit var animatorPlay: ObjectAnimator

    companion object {

        private var instance: FloatPlayer? = null

        @Synchronized
        fun getInstance(): FloatPlayer {
            if (instance == null) instance = FloatPlayer()
            return instance as FloatPlayer
        }
    }

    //显示播放控件
    fun show(context: Context) {
        if (isHideFloatPlayer) return
        mContext = context
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        init()
        windowManager.addView(mViewRoot, layoutParam)
        isShowing = true

        if (!isExpansion) playViewShrink()
        if (isPlaying) playControlStatusSwitch(true)
    }

    fun dismiss(context: Context) {
        if (!isShowing) return
        val windowManger = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManger.removeView(mViewRoot)
        isShowing = false
        mContext = null
    }

    //开启展示播放控件
    fun open(context: Context) {
        isHideFloatPlayer = false
        show(context)
    }

    //关闭播放控件
    fun close() {
        if (isHideFloatPlayer || mContext == null) return
        dismiss(mContext!!)
        isHideFloatPlayer = true
    }

    private fun init() {

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
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24f,
                FloatWindowApp.getAppContext().resources.displayMetrics
            )
                .toInt()
        layoutParam.y =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80f,
                FloatWindowApp.getAppContext().resources.displayMetrics
            )
                .toInt()

        mViewRoot = LayoutInflater.from(FloatWindowApp.getAppContext())
            .inflate(R.layout.float_player_view, null)
        mCsRoot = mViewRoot.findViewById(R.id.csRootFloatPlayer)
        mCsApply.clone(mCsRoot)
        mCsReset.clone(mCsRoot)

        mViewRoot.findViewById<ShapeableImageView>(R.id.sivPlayerCover).setOnClickListener {
            playExpansionStatusSwitch(!isExpansion)
        }

        mViewRoot.findViewById<ImageView>(R.id.ivPlayerControl).setOnClickListener {
            playControlStatusSwitch(!isPlaying)
        }

        mViewRoot.findViewById<ImageView>(R.id.ivPlayerNext)
            .setOnClickListener {
                //TODO 下一首
                Toast.makeText(mContext, "下一首", Toast.LENGTH_SHORT).show()
            }
        mViewRoot.findViewById<ImageView>(R.id.ivPlayerClose).setOnClickListener {
            playControlStatusSwitch(false)
            close()
        }

        initRotationAnimator(mViewRoot.findViewById<ImageView>(R.id.sivPlayerCover))

        isViewInit = true
    }

    //播放按钮状态控制
    private fun playControlStatusSwitch(startPlay: Boolean) {

        val ivControl = mViewRoot.findViewById<ImageView>(R.id.ivPlayerControl)
        if (startPlay) animStart() else animEnd()
        ivControl.setImageResource(
            if (startPlay) R.drawable.ic_baseline_pause_24
            else R.drawable.ic_baseline_play_arrow_24
        )
        isPlaying = startPlay
    }

    //初始化旋转动画
    private fun initRotationAnimator(target: View) {
        //顺时针
        animatorPlay = ObjectAnimator.ofFloat(target, "rotation", 0f, 360f)
        //3s一圈
        animatorPlay.duration = 6000
        animatorPlay.repeatMode = ValueAnimator.RESTART
        animatorPlay.repeatCount = ValueAnimator.INFINITE
        animatorPlay.interpolator = LinearInterpolator()
    }

    //开始播放
    private fun animStart() {
        animatorPlay.start()
    }

    //取消播放
    private fun animEnd() {
        animatorPlay.cancel()
    }

    /**
     * 封面点击切换状态
     * @param expansion 展开
     * */
    private fun playExpansionStatusSwitch(expansion: Boolean) {
        if (expansion == isExpansion) return
        if (expansion) playViewReset() else playViewShrink()
        isExpansion = expansion
    }

    //展开播放控件
    private fun playViewReset() {
        TransitionManager.beginDelayedTransition(mCsRoot)
        mCsReset.applyTo(mCsRoot)
    }

    //收缩播放控件
    private fun playViewShrink() {
        TransitionManager.beginDelayedTransition(mCsRoot)
        mCsApply.setVisibility(R.id.ivPlayerClose, View.GONE)
        mCsApply.setVisibility(R.id.ivPlayerNext, View.GONE)
        mCsApply.setVisibility(R.id.ivPlayerControl, View.GONE)
        mCsApply.applyTo(mCsRoot)
    }
}