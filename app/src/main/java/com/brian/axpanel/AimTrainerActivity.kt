package com.brian.axpanel
import android.content.Context; import android.graphics.*; import android.graphics.drawable.GradientDrawable
import android.media.*; import android.os.*; import android.view.*; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class AimTrainerActivity : AppCompatActivity() {
    private var hits = 0; private var taps = 0; private var active = false; private var timer: CountDownTimer? = null
    private fun box(bg: String, st: String, r: Float = 24f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        var best = prefs.getInt("aim_best", 0)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0B0709")); setPadding(32, 40, 32, 32) }
        val tvTop = TextView(this).apply { text = "AIM WARMUP ARENA • Rekor: $best"; textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")) }
        val tvStat = TextView(this).apply { text = "Waktu: 30s  |  Skor: 0  |  Akurasi: 0%"; textSize = 13f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 8, 0, 16) }
        root.addView(tvTop); root.addView(tvStat)
        val arena = FrameLayout(this).apply { background = box("#12131A", "#99FF2A32", 28f); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val target = View(this).apply {
            background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor("#E50914")); setStroke(4, Color.WHITE) }
            layoutParams = FrameLayout.LayoutParams(110, 110); visibility = View.GONE
        }
        fun moveTarget() {
            val w = (arena.width - 130).coerceAtLeast(100); val h = (arena.height - 130).coerceAtLeast(100)
            target.x = (20..w).random().toFloat(); target.y = (20..h).random().toFloat()
        }
        fun updateStat(sec: Long) {
            val acc = if (taps == 0) 0 else (hits * 100) / taps
            tvStat.text = "Waktu: ${sec}s  |  Skor: $hits  |  Akurasi: $acc%"
        }
        target.setOnClickListener {
            if (!active) return@setOnClickListener
            hits++; taps++; moveTarget(); updateStat(0)
        }
        arena.setOnClickListener { if (active) taps++ }
        arena.addView(target); root.addView(arena)
        val btnRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 16, 0, 8) }
        val btnStart = Button(this).apply {
            text = "MULAI WARMUP (30s)"; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 20f)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(0, 0, 8, 0) }
            setOnClickListener {
                timer?.cancel(); hits = 0; taps = 0; active = true; target.visibility = View.VISIBLE; moveTarget()
                timer = object : CountDownTimer(30000, 1000) {
                    override fun onTick(ms: Long) { updateStat(ms / 1000) }
                    override fun onFinish() {
                        active = false; target.visibility = View.GONE
                        if (hits > best) { best = hits; prefs.edit().putInt("aim_best", best).apply(); tvTop.text = "AIM WARMUP ARENA • Rekor: $best" }
                        val acc = if (taps == 0) 0 else (hits * 100) / taps
                        tvStat.text = "SELESAI! Skor: $hits  |  Akurasi: $acc%  |  Rekor: $best"
                    }
                }.start()
            }
        }
        fun playSide(isLeft: Boolean) = Thread {
            try {
                val sr = 44100; val n = sr / 2; val buf = ShortArray(n * 2)
                for (i in 0 until n) { val v = (Math.sin(2.0 * Math.PI * i / (sr / 520.0)) * 16000).toInt().toShort(); if (isLeft) buf[i * 2] = v else buf[i * 2 + 1] = v }
                @Suppress("DEPRECATION") val at = AudioTrack(AudioManager.STREAM_MUSIC, sr, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, buf.size * 2, AudioTrack.MODE_STATIC)
                at.write(buf, 0, buf.size); at.play(); Thread.sleep(550); at.release()
            } catch (_: Exception) {}
        }.start()
        btnRow.addView(btnStart)
        btnRow.addView(Button(this).apply { text = "🎧 L"; setTextColor(Color.WHITE); background = box("#CC120E16", "#00D2FF", 20f); setOnClickListener { playSide(true) } })
        btnRow.addView(Button(this).apply { text = "🎧 R"; setTextColor(Color.WHITE); background = box("#CC120E16", "#00D2FF", 20f); setOnClickListener { playSide(false) } })
        root.addView(btnRow); setContentView(root)
    }
    override fun onDestroy() { super.onDestroy(); timer?.cancel() }
}
