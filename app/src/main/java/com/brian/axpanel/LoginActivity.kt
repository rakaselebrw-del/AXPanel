package com.brian.axpanel
import android.content.Intent; import android.graphics.Color; import android.os.Bundle; import android.widget.*; import androidx.appcompat.app.AppCompatActivity
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AuthManager.currentUser != null) { startActivity(Intent(this, MainActivity::class.java)); finish(); return }
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0D151C")); setPadding(64, 150, 64, 64) }
        val title = TextView(this).apply { text = "AX PANEL LOGIN"; textSize = 24f; setTextColor(Color.parseColor("#00A8E8")); setPadding(0, 0, 0, 48) }
        val etUser = EditText(this).apply { hint = "Username"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
        val etPass = EditText(this).apply { hint = "Password"; inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
        val btnLogin = Button(this).apply {
            text = "MASUK"; setBackgroundColor(Color.parseColor("#00A8E8")); setTextColor(Color.WHITE)
            setOnClickListener {
                val user = AuthManager.login(this@LoginActivity, etUser.text.toString(), etPass.text.toString())
                if (user != null) {
                    Toast.makeText(this@LoginActivity, "Selamat datang ${user.username} [${user.role.label}]", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java)); finish()
                } else Toast.makeText(this@LoginActivity, "Username atau password salah!", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(title); layout.addView(etUser); layout.addView(etPass); layout.addView(btnLogin); setContentView(layout)
    }
}
