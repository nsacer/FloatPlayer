package com.example.floatplayer

import android.util.AttributeSet
import android.view.View
import android.graphics.Paint
import android.util.TypedValue
import android.graphics.Color
import android.graphics.Canvas
import android.animation.ValueAnimator
import com.example.floatplayer.BgView.PositiveEvaluator
import android.animation.ValueAnimator.AnimatorUpdateListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import android.animation.TypeEvaluator
import android.content.Context
import com.example.floatplayer.BgView.NegativeEvaluator

class BgView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mWidth = 0f
    private var mRadius = 0f
    private var mPositionEndX = 0f
    private var mPaint: Paint? = null

    //是否展开
    private var bExpend = true

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mRadius = h / 2f
        mPositionEndX = mWidth - mRadius
        mPaint!!.strokeWidth = h.toFloat()
    }

    private fun initPaint() {
        val fPaint = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f,
            resources.displayMetrics
        )
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.color = Color.BLUE
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.strokeWidth = fPaint
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
        if (bExpend) {
            doNegAnimation()
        } else {
            doPosAnimation()
        }
        bExpend = !bExpend
    }

    //动画
    private fun doPosAnimation() {
        val animator = ValueAnimator.ofObject(PositiveEvaluator(), mRadius, mWidth - mRadius)
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            mPositionEndX = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.duration = 400
        animator.interpolator = FastOutSlowInInterpolator()
        animator.start()
    }

    private inner class PositiveEvaluator : TypeEvaluator<Float> {
        override fun evaluate(v: Float, startValue: Float, endValue: Float): Float {
            return startValue + v * (endValue - startValue)
        }
    }

    /**
     * 数据为正数的时候动画
     */
    private fun doNegAnimation() {
        val animator = ValueAnimator.ofObject(NegativeEvaluator(), mRadius, mWidth - mRadius)
        animator.addUpdateListener { valueAnimator: ValueAnimator ->
            mPositionEndX = valueAnimator.animatedValue as Float
            invalidate()
        }
        animator.duration = 400
        animator.interpolator = FastOutSlowInInterpolator()
        animator.start()
    }

    private inner class NegativeEvaluator : TypeEvaluator<Float> {
        override fun evaluate(v: Float, startValue: Float, endValue: Float): Float {
            return endValue - v * (endValue - startValue)
        }
    }

    init {
        initPaint()
    }
}