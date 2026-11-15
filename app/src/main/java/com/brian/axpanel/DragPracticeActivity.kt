package com.brian.axpanel
import android.content.Context; import android.graphics.*; import android.graphics.drawable.GradientDrawable
import android.os.*; import android.view.*; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
import kotlin.math.hypot
class DragPracticeActivity : AppCompatActivity() {
    private var hs = 0; private var total = 0; private var streak = 0; private var score = 0
    private lateinit var tvInfo: TextView; private lateinit var arena: ArenaView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0B0709")); setPadding(32, 40, 32, 32) }
        root.addView(TextView(this).apply { text = "LATIHAN FLICK & HEADSHOT"; textSize = 18f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")) })
        tvInfo = TextView(this).apply { text = "Ketuk lingkaran MERAH (Kepala) secepat mungkin!"; textSize = 13f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 8, 0, 16) }
        root.addView(tvInfo)
        arena = ArenaView(this).apply {
            background = GradientDrawable().apply { setColor(Color.parseColor("#12131A")); cornerRadius = 28f; setStroke(2, Color.parseColor("#2D181E")) }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        root.addView(arena)
        root.addView(Button(this).apply {
            text = "RESET SKOR LATIHAN"; setTextColor(Color.WHITE)
            background = GradientDrawable().apply { setColor(Color.parseColor("#E50914")); cornerRadius = 20f }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 16, 0, 0) }
            setOnClickListener { hs = 0; total = 0; streak = 0; score = 0; tvInfo.text = "Skor direset! Ketuk lingkaran MERAH."; arena.spawn() }
        })
        setContentView(root)
    }
    inner class ArenaView(ctx: Context) : View(ctx) {
        private var tx = 300f; private var ty = 400f; private var msg = ""
        private val pBody = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#3A3D4D") }
        private val pHead = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E50914") }
        private val pRing = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 3f }
        private val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFD600"); textSize = 42f; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER }
        fun spawn() {
            if (width > 200 && height > 250) {
                tx = (110..(width - 110)).random().toFloat()
                ty = (160..(height - 130)).random().toFloat()
            }
            invalidate()
        }
        override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) { super.onSizeChanged(w, h, ow, oh); spawn() }
        override fun onTouchEvent(ev: MotionEvent): Boolean {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                total++
                val dHead = hypot(ev.x - tx, ev.y - (ty - 75f))
                val dBody = hypot(ev.x - tx, ev.y - ty)
                when {
                    dHead <= 42f -> { hs++; streak++; score += 100; msg = "HEADSHOT! x$streak"; spawn() }
                    dBody <= 78f -> { streak = 0; score += 25; msg = "BODY (+25)"; spawn() }
                    else -> { streak = 0; msg = "MISS"; invalidate() }
                }
                val rate = if (total == 0) 0 else (hs * 100) / total
                tvInfo.text = "Skor: $score  |  HS Rate: $rate%  |  Streak: $streak"
            }
            return true
        }
        override fun onDraw(c: Canvas) {
            super.onDraw(c)
            c.drawCircle(tx, ty, 65f, pBody)
            c.drawCircle(tx, ty - 75f, 34f, pHead)
            c.drawCircle(tx, ty - 75f, 34f, pRing)
            if (msg.isNotEmpty()) c.drawText(msg, width / 2f, 80f, pTxt)
        }
    }
}
