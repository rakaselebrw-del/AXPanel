package com.brian.axpanel
import android.animation.ObjectAnimator; import android.content.Intent; import android.content.res.ColorStateList
import android.graphics.*; import android.os.*; import android.view.Gravity; import android.view.animation.OvershootInterpolator
import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class LaunchGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra("pkg") ?: "com.dts.freefireth"
        val gName = intent.getStringExtra("name") ?: "Free Fire"
        val wrap = FrameLayout(this).apply { setBackgroundColor(Color.parseColor("#080508")) }
        val bg = ImageView(this).apply { setImageResource(R.drawable.skeleton_king); scaleType = ImageView.ScaleType.CENTER_CROP; alpha = 0f; layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT) }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(64, 48, 64, 48) }
        val icon = ImageView(this).apply { setImageResource(R.drawable.skeleton_king); scaleType = ImageView.ScaleType.CENTER_CROP; layoutParams = LinearLayout.LayoutParams(340, 200); alpha = 0f; scaleX = 0.6f; scaleY = 0.6f }
        val tv1 = TextView(this).apply { text = "MEMASUKI ${gName.uppercase()}..."; textSize = 20f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#D4AF37")); gravity = Gravity.CENTER; setPadding(0, 24, 0, 8); alpha = 0f }
        val tv2 = TextView(this).apply { text = "YAN DELUXION ENGINE • OPTIMASI AKTIF"; textSize = 11f; setTextColor(Color.parseColor("#00D2FF")); gravity = Gravity.CENTER; setPadding(0, 0, 0, 24) }
        val bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100; progress = 0; progressTintList = ColorStateList.valueOf(Color.parseColor("#E50914")); progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A181E")); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) }
        root.addView(icon); root.addView(tv1); root.addView(tv2); root.addView(bar)
        wrap.addView(bg); wrap.addView(root); setContentView(wrap)
        bg.animate().alpha(0.45f).setDuration(700).start()
        icon.animate().alpha(1f).scaleX(1f).scaleY(1f).setInterpolator(OvershootInterpolator()).setDuration(700).start()
        tv1.animate().alpha(1f).setStartDelay(150).setDuration(500).start()
        ObjectAnimator.ofInt(bar, "progress", 0, 100).setDuration(1400).start()
        if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("cmd power set-fixed-performance-mode-enabled true") }.start()
        Handler(Looper.getMainLooper()).postDelayed({
            val launch = packageManager.getLaunchIntentForPackage(pkg)
            if (launch != null) { startActivity(launch); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) }
            else Toast.makeText(this, "$gName belum terinstal di HP Anda!", Toast.LENGTH_LONG).show()
            finish()
        }, 1550)
    }
}
