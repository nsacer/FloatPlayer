package com.example.floatplayer

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * 播放器自定义的背景View：因为Constraint动画有显示瑕疵
 * */
class PlayerBgView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //控件宽度
    private var mWidth = 0f

    //背景圆形半径（控件高度一半）
    private var mRadius = 0f

    //动画执行中的终点坐标
    private var mPositionEndX = 0f
    private var mPaint: Paint? = null

    //是否展开状态
    private var bExpend = true

    //背景颜色
    private val mColorBG = Color.parseColor("#D7D7D7")

    //动画时长
    private val mTimeAnim = 400L

    //展开后终点坐标x，固定
    private var mEndX = 0f

    init {
        initPaint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mRadius = h / 2f
        mPositionEndX = mWidth - mRadius
        mEndX = mPositionEndX
        measurePaintStrokeWidth(h.toFloat())
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.color = mColorBG
        mPaint!!.strokeCap = Paint.Cap.ROUND
    }

    //设置画笔宽度为视图高度
    private fun measurePaintStrokeWidth(height: Float) {
        mPaint?.strokeWidth = height
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBg(canvas)
    }

    private fun drawBg(canvas: Canvas) {
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint!!)
        if (mPositionEndX == 0f) return
        canvas.drawLine(mRadius, mRadius, mPositionEndX, mRadius, mPaint!!)
    }

    fun doAnimation() {
        if (bExpend) doShrinkAnimation() else doExpandAnimation()
        bExpend = !bExpend
    }

    //展开动画
    private fun doExpandAnimation() {
        val animator = ValueAnimator.ofObject(PositiveEvaluator(), mRadius, mEndX)
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            mPositionEndX = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.duration = mTimeAnim
        animator.interpolator = FastOutSlowInInterpolator()
        animator.start()
    }

    private inner class PositiveEvaluator : TypeEvaluator<Float> {
        override fun evaluate(v: Float, startValue: Float, endValue: Float): Float {
            return startValue + v * (endValue - startValue)
        }
    }

    //收缩动画
    private fun doShrinkAnimation() {
        val animator = ValueAnimator.ofObject(NegativeEvaluator(), mRadius, mEndX)
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            mPositionEndX = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.duration = mTimeAnim
        animator.interpolator = FastOutSlowInInterpolator()
        animator.start()
    }

    private inner class NegativeEvaluator : TypeEvaluator<Float> {
        override fun evaluate(v: Float, startValue: Float, endValue: Float): Float {
            return endValue - v * (endValue - startValue)
        }
    }
}