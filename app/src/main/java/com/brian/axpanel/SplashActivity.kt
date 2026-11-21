package com.brian.axpanel
import android.animation.ObjectAnimator; import android.content.Intent; import android.content.res.ColorStateList
import android.graphics.*; import android.os.*; import android.view.Gravity; import android.view.animation.OvershootInterpolator
import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val wrap = FrameLayout(this).apply { setBackgroundColor(Color.parseColor("#080508")) }
        val bg = ImageView(this).apply { setImageResource(R.drawable.skeleton_king); scaleType = ImageView.ScaleType.CENTER_CROP; alpha = 0f; layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT) }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(64, 48, 64, 48) }
        val logo = ImageView(this).apply { setImageResource(R.drawable.skeleton_king); scaleType = ImageView.ScaleType.CENTER_CROP; layoutParams = LinearLayout.LayoutParams(420, 240); alpha = 0f; scaleX = 0.4f; scaleY = 0.4f }
        val title = TextView(this).apply { text = "YAN DELUXION"; textSize = 28f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#D4AF37")); gravity = Gravity.CENTER; setPadding(0, 24, 0, 6); alpha = 0f; translationY = 40f }
        val sub = TextView(this).apply { text = "BY BRIAN • ROYAL EDITION"; textSize = 11f; setTextColor(Color.parseColor("#FF2A32")); gravity = Gravity.CENTER; setPadding(0, 0, 0, 24); alpha = 0f }
        val bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100; progress = 0; progressTintList = ColorStateList.valueOf(Color.parseColor("#D4AF37")); progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A181E")); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) }
        root.addView(logo); root.addView(title); root.addView(sub); root.addView(bar)
        wrap.addView(bg); wrap.addView(root); setContentView(wrap)
        bg.animate().alpha(0.38f).setDuration(900).start()
        logo.animate().alpha(1f).scaleX(1f).scaleY(1f).setInterpolator(OvershootInterpolator()).setDuration(850).start()
        title.animate().alpha(1f).translationY(0f).setStartDelay(250).setDuration(600).start()
        sub.animate().alpha(1f).setStartDelay(450).setDuration(550).start()
        ObjectAnimator.ofInt(bar, "progress", 0, 100).setDuration(1500).start()
        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (AuthManager.currentUser != null || AuthManager.restoreSession(this)) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, next)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish()
        }, 1700)
    }
}
