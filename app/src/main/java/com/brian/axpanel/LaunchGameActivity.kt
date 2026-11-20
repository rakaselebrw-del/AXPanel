package com.brian.axpanel
import android.animation.ObjectAnimator; import android.content.Context; import android.content.Intent; import android.content.res.ColorStateList
import android.graphics.*; import android.graphics.drawable.GradientDrawable; import android.net.Uri; import android.os.*
import android.view.*; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class LaunchGameActivity : AppCompatActivity() {
    private var launched = false; private val h = Handler(Looper.getMainLooper()); private var pkg = "com.dts.freefireth"; private var gName = "Free Fire"
    private fun goGame() {
        if (launched) return; launched = true; h.removeCallbacksAndMessages(null)
        val i = packageManager.getLaunchIntentForPackage(pkg)
        if (i != null) { startActivity(i); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) }
        else Toast.makeText(this, "$gName belum terinstal!", Toast.LENGTH_LONG).show()
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pkg = intent.getStringExtra("pkg") ?: "com.dts.freefireth"
        gName = intent.getStringExtra("name") ?: "Free Fire"
        if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("cmd power set-fixed-performance-mode-enabled true") }.start()
        val wrap = FrameLayout(this).apply { setBackgroundColor(Color.parseColor("#060407")) }
        val vidUri = getSharedPreferences("ax_prefs", MODE_PRIVATE).getString("intro_video_uri", null)
        if (vidUri != null) {
            val vv = VideoView(this).apply { layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER) }
            wrap.addView(vv)
            try {
                vv.setVideoURI(Uri.parse(vidUri))
                vv.setOnPreparedListener { mp -> mp.isLooping = false; vv.start() }
                vv.setOnCompletionListener { goGame() }
                vv.setOnErrorListener { _, _, _ -> goGame(); true }
                h.postDelayed({ goGame() }, 6500)
            } catch (_: Exception) { h.postDelayed({ goGame() }, 1200) }
        } else {
            val bg = ImageView(this).apply { setImageResource(R.drawable.skeleton_king); scaleType = ImageView.ScaleType.CENTER_CROP; alpha = 0.5f; layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT) }
            wrap.addView(bg); wrap.addView(MotionFxView(this))
            bg.animate().scaleX(1.18f).scaleY(1.18f).setDuration(2000).start()
            h.postDelayed({ goGame() }, 2100)
        }
        val ov = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL; setPadding(48, 48, 48, 64) }
        ov.addView(TextView(this).apply { text = "MEMASUKI ${gName.uppercase()}..."; textSize = 18f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#D4AF37")); gravity = Gravity.CENTER })
        val bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100; progress = 0; progressTintList = ColorStateList.valueOf(Color.parseColor("#E50914")); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 12, 0, 16) } }
        ov.addView(bar); ObjectAnimator.ofInt(bar, "progress", 0, 100).setDuration(if (vidUri != null) 4500 else 1900).start()
        ov.addView(Button(this).apply { text = "LEWATI ▶"; textSize = 11f; setTextColor(Color.WHITE); background = GradientDrawable().apply { setColor(Color.parseColor("#AA1A0B0E")); cornerRadius = 30f; setStroke(2, Color.parseColor("#D4AF37")) }; setOnClickListener { goGame() } })
        wrap.addView(ov); setContentView(wrap)
    }
    override fun onDestroy() { super.onDestroy(); h.removeCallbacksAndMessages(null) }
    inner class MotionFxView(ctx: Context) : View(ctx) {
        private var ang = 0f; private val pGold = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D4AF37"); style = Paint.Style.STROKE; strokeWidth = 5f }
        private val pRed = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF1E27"); style = Paint.Style.STROKE; strokeWidth = 4f }
        private val pGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF4D57") }
        private val pts = List(22) { floatArrayOf((0..1000).random() / 1000f, (0..1000).random() / 1000f, (4..10).random().toFloat()) }
        private val r = RectF()
        override fun onDraw(c: Canvas) {
            super.onDraw(c); ang = (ang + 4f) % 360f
            val cx = width / 2f; val cy = height / 2f - 40f
            r.set(cx - 170f, cy - 170f, cx + 170f, cy + 170f); c.drawArc(r, ang, 110f, false, pGold); c.drawArc(r, ang + 180f, 110f, false, pGold)
            r.set(cx - 125f, cy - 125f, cx + 125f, cy + 125f); c.drawArc(r, -ang * 1.4f, 140f, false, pRed)
            pts.forEach { pt -> pt[1] -= 0.008f; if (pt[1] < 0f) pt[1] = 1f; c.drawCircle(pt[0] * width, pt[1] * height, pt[2], pGlow) }
            postInvalidateOnAnimation()
        }
    }
}
