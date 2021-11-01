package com.example.floatplayer

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * 音频播放器背景View
 * */
class PlayerBgView(context: Context, attributeSet: AttributeSet) : View(context) {

    private var mWidth = 0
    private var mHeight = 0
    private lateinit var mPaint: Paint
    private var mEndX = 0f
    private var mCenterY = 0f

    private fun initPaint() {

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.color = Color.parseColor("#999")
        mPaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 48f,
            resources.displayMetrics
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        mWidth = w
//        mHeight = h
//        mEndX = mWidth.toFloat()
//        mCenterY = (mHeight / 2f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.drawColor(Color.parseColor("#0987a8"))
//        drawView(canvas)
    }

    private fun drawView(canvas: Canvas?) {
        if (canvas == null) return
        canvas.drawLine(0f, mCenterY, mEndX, mCenterY, mPaint)
    }

    //展开插值器
    private class PositiveEvaluator : TypeEvaluator<Float> {
        override fun evaluate(v: Float, startValue: Float, endValue: Float): Float {
            return startValue + v * (endValue - startValue)
        }
    }

    //收缩插值器
    private class NegativeEvaluator : TypeEvaluator<Float> {
        override fun evaluate(v: Float, startValue: Float, endValue: Float): Float {
            return endValue - v * (endValue - startValue)
        }
    }

    //展开动画
    private fun doExpendAnim() {
        val animator = ValueAnimator.ofObject(PositiveEvaluator(), 0, mWidth.toFloat())
        animator.addUpdateListener {
            mEndX = it.animatedValue as Float
            invalidate()
        }
        animator.duration = 200
        animator.start()
    }

    //收缩动画
    private fun doShrinkAnim() {
        val animator = ValueAnimator.ofObject(NegativeEvaluator(), 0, mHeight.toFloat())
        animator.addUpdateListener {
            mEndX = it.animatedValue as Float
            invalidate()
        }
        animator.duration = 200
        animator.start()
    }

    //收缩
    private fun shrink() {
        doShrinkAnim()
    }

    //展开
    private fun expend() {
        doExpendAnim()
    }
}