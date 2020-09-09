package com.simple.chris.pebble.functions

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import androidx.transition.PathMotion
import com.simple.chris.pebble.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TransitionArcMotion:android.transition.PathMotion {
    private var curveRadius = 0f
    private val DEFAULT_RADIUS = 100

    constructor(){

    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TransitionArcMotion)
        curveRadius = a.getInteger(R.styleable.TransitionArcMotion_arcRadius, DEFAULT_RADIUS).toFloat()
        a.recycle()
    }

    override fun getPath(startX: Float, startY: Float, endX: Float, endY: Float): Path {
        val arcPath = Path()
        val midX = startX + ((endX - startX) / 2)
        val midY = startY + ((endY - startY) / 2)
        val xDiff = midX - startX
        val yDiff = midY - startY
        val angle = (atan2(yDiff.toDouble(), xDiff.toDouble()) * (180 / Math.PI)) - 90
        val angleRadians = Math.toRadians(angle)
        val pointX = (midX + curveRadius * cos(angleRadians)).toFloat()
        val pointY = (midY + curveRadius * sin(angleRadians)).toFloat()
        arcPath.moveTo(startX, startY)
        arcPath.cubicTo(startX, startY, pointX, pointY, endX, endY)
        return arcPath
    }
}