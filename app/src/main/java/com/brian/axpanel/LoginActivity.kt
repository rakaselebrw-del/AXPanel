package com.brian.axpanel
import android.content.Intent; import android.graphics.Color; import android.os.Bundle; import android.view.Gravity; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AuthManager.currentUser != null) { startActivity(Intent(this, MainActivity::class.java)); finish(); return }
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL; setBackgroundColor(Color.parseColor("#0A0607")); setPadding(64, 120, 64, 64) }
        val logo = ImageView(this).apply { setImageResource(R.drawable.ym_logo); layoutParams = LinearLayout.LayoutParams(280, 280).apply { setMargins(0, 0, 0, 24) } }
        val title = TextView(this).apply { text = "YM GAMING PANEL"; textSize = 22f; setTextColor(Color.parseColor("#FF1E27")); setPadding(0, 0, 0, 40) }
        val etUser = EditText(this).apply { hint = "Username"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) }
        val etPass = EditText(this).apply { hint = "Password"; inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) }
        val btnLogin = Button(this).apply {
            text = "MASUK"; setBackgroundColor(Color.parseColor("#D90416")); setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 32, 0, 0) }
            setOnClickListener {
                val user = AuthManager.login(this@LoginActivity, etUser.text.toString(), etPass.text.toString())
                if (user != null) { Toast.makeText(this@LoginActivity, "Selamat datang ${user.username} [${user.role.label}]", Toast.LENGTH_SHORT).show(); startActivity(Intent(this@LoginActivity, MainActivity::class.java)); finish() }
                else Toast.makeText(this@LoginActivity, "Username atau password salah!", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(logo); layout.addView(title); layout.addView(etUser); layout.addView(etPass); layout.addView(btnLogin); setContentView(layout)
    }
}
