
package com.wellness.habittracker.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.wellness.habittracker.models.MoodEntry

class MoodChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): View(context, attrs) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = 6f; style = Paint.Style.STROKE }
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private var data: List<MoodEntry> = emptyList()

    fun setData(entries: List<MoodEntry>) {
        data = entries.takeLast(14)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return
        val w = width.toFloat(); val h = height.toFloat()
        val minY = 1f; val maxY = 5f
        val stepX = if (data.size>1) w/(data.size-1) else w
        val path = Path()
        for ((i, e) in data.withIndex()) {
            val x = i * stepX
            val yRatio = (e.mood - minY) / (maxY - minY)
            val y = h - (yRatio * (h * 0.9f)) - h*0.05f
            if (i==0) path.moveTo(x,y) else path.lineTo(x,y)
            canvas.drawCircle(x, y, 6f, pointPaint)
        }
        canvas.drawPath(path, linePaint)
    }
}
