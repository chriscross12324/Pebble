package com.simple.chris.pebble

import android.graphics.Outline
import android.graphics.Rect
import android.view.View
import android.view.ViewOutlineProvider

/*
inner class OutlineProvider (
        private val rect: Rect = Rect(),
        var scaleX: Float,
        var scaleY: Float,
        var yShift: Int
) : ViewOutlineProvider() {
    override fun getOutline(p0: View?, p1: Outline?) {
        p0?.background?.copyBounds(rect)
        rect.scale(scaleX, scaleY)
        rect.offset(0, yShift)

        val cornerRadius =
                resources.getDimensionsPixelSize(R.dimen.control_corner_material).toFloat()

        p1?.setRoundRect(rect, cornerRadius)
    }
}*/
