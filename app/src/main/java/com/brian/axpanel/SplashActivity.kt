package com.brian.axpanel
import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface
import android.os.*; import android.view.Gravity; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setBackgroundColor(Color.parseColor("#080609")); setPadding(48, 48, 48, 48) }
        val logo = ImageView(this).apply { setImageResource(R.drawable.skeleton_king); layoutParams = LinearLayout.LayoutParams(360, 360); alpha = 0f; scaleX = 0.55f; scaleY = 0.55f }
        val title = TextView(this).apply { text = "YAN DELUXION"; textSize = 27f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#D4AF37")); gravity = Gravity.CENTER; setPadding(0, 28, 0, 8); alpha = 0f; translationY = 36f }
        val sub = TextView(this).apply { text = "ROYAL SKELETON EDITION"; textSize = 11f; setTextColor(Color.parseColor("#FF2A32")); gravity = Gravity.CENTER; alpha = 0f; translationY = 36f }
        root.addView(logo); root.addView(title); root.addView(sub); setContentView(root)
        logo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(750).start()
        title.animate().alpha(1f).translationY(0f).setStartDelay(250).setDuration(650).start()
        sub.animate().alpha(1f).translationY(0f).setStartDelay(450).setDuration(600).start()
        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (AuthManager.currentUser != null || AuthManager.restoreSession(this)) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, next)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish()
        }, 1650)
    }
}
