package com.brian.axpanel
import android.content.Context; import android.graphics.*; import android.graphics.drawable.GradientDrawable
import android.os.*; import android.view.*; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class GamingLabActivity : AppCompatActivity() {
    private lateinit var tvFps: TextView; private var frames = 0; private var lastT = 0L; private var running = true
    private val cb = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!running) return
            frames++; val now = System.currentTimeMillis()
            if (now - lastT >= 1000) { tvFps.text = "Live Screen FPS: $frames fps"; frames = 0; lastT = now }
            Choreographer.getInstance().postFrameCallback(this)
        }
    }
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0B0709")); setPadding(36, 48, 36, 36) }
        root.addView(TextView(this).apply { text = "YM GAMING LAB • REFLEX & TOUCH"; textSize = 18f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 20) })
        var state = 0; var goTime = 0L; var best = 9999L; val h = Handler(Looper.getMainLooper())
        val tvBest = TextView(this).apply { text = "Rekor Tercepat: - ms"; textSize = 13f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 12) }
        val btnReflex = Button(this).apply {
            text = "TAP UNTUK MULAI TES REFLEKS"; textSize = 15f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#15161E", "#FF3B30", 26f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 240).apply { setMargins(0, 0, 0, 24) }
        }
        btnReflex.setOnClickListener {
            when (state) {
                0 -> {
                    state = 1; btnReflex.text = "TUNGGU HIJAU..."; btnReflex.background = box("#3A0A12", "#FF3B30", 26f)
                    h.postDelayed({ if (state == 1) { state = 2; goTime = System.currentTimeMillis(); btnReflex.text = "TAP SEKARANG!"; btnReflex.background = box("#00C853", "#00E676", 26f) } }, (1200..3000).random().toLong())
                }
                1 -> { state = 0; h.removeCallbacksAndMessages(null); btnReflex.text = "TERLALU CEPAT! TAP ULANG"; btnReflex.background = box("#15161E", "#FF3B30", 26f) }
                2 -> {
                    val ms = System.currentTimeMillis() - goTime; state = 0
                    if (ms < best) { best = ms; tvBest.text = "Rekor Tercepat: $best ms" }
                    btnReflex.text = "${ms} ms!\n(Tap untuk coba lagi)"; btnReflex.background = box("#15161E", "#00D2FF", 26f)
                }
            }
        }
        root.addView(tvBest); root.addView(btnReflex)
        tvFps = TextView(this).apply { text = "Live Screen FPS: -- fps"; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00E676")); setPadding(0, 8, 0, 12) }
        root.addView(tvFps)
        root.addView(TextView(this).apply { text = "TES MULTI-TOUCH (Sentuh layar dengan banyak jari):"; textSize = 12f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 8) })
        root.addView(TouchCanvas(this).apply { background = box("#12131A", "#2D181E", 26f); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) })
        setContentView(root); lastT = System.currentTimeMillis(); Choreographer.getInstance().postFrameCallback(cb)
    }
    override fun onDestroy() { super.onDestroy(); running = false; Choreographer.getInstance().removeFrameCallback(cb) }
    inner class TouchCanvas(ctx: Context) : View(ctx) {
        private val pts = mutableListOf<Pair<Float, Float>>()
        private val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF2A32"); style = Paint.Style.FILL }
        private val pt = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; textSize = 38f; typeface = Typeface.DEFAULT_BOLD }
        override fun onTouchEvent(ev: MotionEvent): Boolean {
            pts.clear()
            if (ev.actionMasked != MotionEvent.ACTION_UP && ev.actionMasked != MotionEvent.ACTION_CANCEL) {
                for (i in 0 until ev.pointerCount) {
                    if (ev.actionMasked == MotionEvent.ACTION_POINTER_UP && i == ev.actionIndex) continue
                    pts.add(ev.getX(i) to ev.getY(i))
                }
            }
            invalidate(); return true
        }
        override fun onDraw(c: Canvas) {
            super.onDraw(c)
            c.drawText("Jari Aktif: ${pts.size} Finger", 36f, 64f, pt)
            pts.forEach { (x, y) -> c.drawCircle(x, y, 55f, p) }
        }
    }
}
