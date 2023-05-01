package com.bignerdranch.android.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

private const val TAG = "BoxDrawingView"

class BoxDrawingView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var currentBox: Box? = null
    private val boxes = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = 0x22ff0000.toInt()
    }
    private val backgroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                // Reset the drawing state
                if(boxes.size < 3) {
                    currentBox = Box(current).also {
                        boxes.add(it)
                    }
                }

            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                updateCurrentBox(current)
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                updateCurrentBox(current)
                currentBox = null
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null
            }
        }

        Log.i(TAG, "$action at x=${current.x}, y=${current.y}")

        return true
    }

    private fun updateCurrentBox(current: PointF){
        currentBox?.let {
            val startPoint = it.start
            val deltaX = current.x - startPoint.x
            val deltaY = current.y - startPoint.y
            it.width = Math.abs(deltaX)
            it.height = Math.abs(deltaY)

            val pointChange = Math.min(it.width, it.height)
            val changeX = if(deltaX < 0) {
                pointChange * -1
            } else {
                pointChange
            }
            val changeY = if(deltaY < 0){
                pointChange * -1
            } else {
                pointChange
            }
            val endPoint = PointF(startPoint.x + changeX, startPoint.y + changeY)

            it.end = endPoint
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        // Fill the background
        canvas.drawPaint(backgroundPaint)

        boxes.forEach { box ->
            if(box.width > box.height) {
                canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
            } else {
                canvas.drawOval(box.left, box.top, box.right, box.bottom, boxPaint)
            }
        }
    }
}