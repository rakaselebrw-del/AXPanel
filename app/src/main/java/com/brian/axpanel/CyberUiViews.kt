package com.brian.axpanel
import android.content.Context; import android.graphics.*
import android.view.View
class CyberGridView(ctx: Context) : View(ctx) {
    private val p = Paint().apply { color = Color.parseColor("#180C10"); strokeWidth = 2f }
    override fun onDraw(c: Canvas) {
        super.onDraw(c); c.drawColor(Color.parseColor("#090608"))
        var x = 0f; while (x < width) { c.drawLine(x, 0f, x, height.toFloat(), p); x += 64f }
        var y = 0f; while (y < height) { c.drawLine(0f, y, width.toFloat(), y, p); y += 64f }
    }
}
class CircularGaugeView(ctx: Context, private var pct: Int, private val title: String, private val valTxt: String, colHex: String) : View(ctx) {
    private val col = Color.parseColor(colHex)
    private val pBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#232533"); style = Paint.Style.STROKE; strokeWidth = 14f; strokeCap = Paint.Cap.ROUND }
    private val pFg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = col; style = Paint.Style.STROKE; strokeWidth = 14f; strokeCap = Paint.Cap.ROUND }
    private val pVal = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; textSize = 34f; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER }
    private val pLbl = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#8E92A4"); textSize = 22f; textAlign = Paint.Align.CENTER }
    private val rect = RectF()
    override fun onMeasure(w: Int, h: Int) { val s = (resources.displayMetrics.widthPixels * 0.25).toInt(); setMeasuredDimension(s, s) }
    override fun onDraw(c: Canvas) {
        super.onDraw(c); val pad = 20f
        rect.set(pad, pad, width - pad, height - pad)
        c.drawArc(rect, -90f, 360f, false, pBg)
        c.drawArc(rect, -90f, (pct.coerceIn(0, 100) * 3.6f), false, pFg)
        c.drawText(valTxt, width / 2f, height / 2f + 6f, pVal)
        c.drawText(title, width / 2f, height / 2f + 34f, pLbl)
    }
}
