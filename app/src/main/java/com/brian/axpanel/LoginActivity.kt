package com.brian.axpanel
import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.Bundle; import android.view.Gravity; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class LoginActivity : AppCompatActivity() {
    private fun box(bg: String, stroke: String, r: Float = 28f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AuthManager.currentUser != null || AuthManager.restoreSession(this)) { startActivity(Intent(this, MainActivity::class.java)); finish(); return }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setBackgroundColor(Color.parseColor("#0B0709")); setPadding(48, 48, 48, 48) }
        val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL; background = box("#15161E", "#2D181E", 36f); setPadding(48, 56, 48, 56); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) }
        card.addView(ImageView(this).apply { setImageResource(R.drawable.skeleton_king); layoutParams = LinearLayout.LayoutParams(180, 180).apply { setMargins(0, 0, 0, 20) } })
        card.addView(TextView(this).apply { text = "YAN DELUXION"; textSize = 24f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); gravity = Gravity.CENTER })
        card.addView(TextView(this).apply { text = "GAMING UTILITY PANEL • 1 USERNAME 1 HANDPHONE"; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); gravity = Gravity.CENTER; setPadding(0, 6, 0, 32) })
        val hwidBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = box("#0E0F14", "#262834", 20f); setPadding(28, 20, 28, 20); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 32) } }
        hwidBox.addView(TextView(this).apply { text = "ID HANDPHONE SAAT INI (HWID)"; textSize = 10f; setTextColor(Color.parseColor("#8E92A4")) })
        hwidBox.addView(TextView(this).apply { text = AuthManager.getHwid(this@LoginActivity); textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")) })
        card.addView(hwidBox)
        fun label(t: String) = TextView(this).apply { text = t; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 8, 0, 8) } }
        fun input(h: String, pass: Boolean = false) = EditText(this).apply {
            hint = h; setTextColor(Color.WHITE); setHintTextColor(Color.parseColor("#5C6070")); background = box("#0D0E13", "#262834", 20f); setPadding(32, 26, 32, 26)
            if (pass) inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(label("USERNAME")); val etUser = input("Masukkan username..."); card.addView(etUser)
        card.addView(label("PASSWORD")); val etPass = input("Masukkan password...", true); card.addView(etPass)
        card.addView(Button(this).apply {
            text = "MASUK KE PANEL"; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 24f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 16, 0, 0) }
            setOnClickListener { btn ->
                btn.isEnabled = false
                Thread {
                    val (user, msg) = AuthManager.login(this@LoginActivity, etUser.text.toString(), etPass.text.toString())
                    runOnUiThread {
                        btn.isEnabled = true
                        if (user != null) { Toast.makeText(this@LoginActivity, "Welcome ${user.username} [${user.role.label}]", Toast.LENGTH_SHORT).show(); startActivity(Intent(this@LoginActivity, MainActivity::class.java)); finish() }
                        else Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }.start()
            }
        })
        root.addView(card); setContentView(root)
    }
}
