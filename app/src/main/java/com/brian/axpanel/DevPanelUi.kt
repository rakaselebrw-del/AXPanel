package com.brian.axpanel
import android.app.Activity; import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.widget.*
import java.net.HttpURLConnection; import java.net.URL
object DevPanelUi {
    private fun box(bg: String, stroke: String, r: Float = 20f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    fun build(act: Activity, parent: LinearLayout) {
        val me = AuthManager.currentUser ?: return
        val allowed = AuthManager.creatableRoles(me.role)
        if (allowed.isEmpty()) return
        val card = LinearLayout(act).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E", 26f); setPadding(32, 28, 32, 28)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 24, 0, 0) }
        }
        card.addView(TextView(act).apply { text = "PANEL ${me.role.label.uppercase()} (KELOLA AKUN & HWID)"; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 12) })
        val tvList = TextView(act).apply { setTextColor(Color.WHITE); textSize = 12f; setPadding(0, 0, 0, 14) }
        fun refreshList() { tvList.text = AuthManager.getAllAccounts().filter { it.role.ordinal > me.role.ordinal || me.role == UserRole.DEV }.joinToString("\n") { "• ${it.username} [${it.role.label}]" } }
        refreshList(); card.addView(tvList)
        val etU = EditText(act).apply { hint = "Username baru"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
        val etP = EditText(act).apply { hint = "Password baru"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
        val spRole = Spinner(act).apply { adapter = ArrayAdapter(act, android.R.layout.simple_spinner_dropdown_item, allowed.map { it.label }) }
        card.addView(etU); card.addView(etP); card.addView(spRole)
        card.addView(Button(act).apply {
            text = "BUAT AKUN"; background = box("#E50914", "#FF3B30", 16f); setTextColor(Color.WHITE)
            setOnClickListener { btn ->
                val role = allowed[spRole.selectedItemPosition]; btn.isEnabled = false
                Thread {
                    val (ok, msg) = AuthManager.addAccount(act, etU.text.toString(), etP.text.toString(), role)
                    act.runOnUiThread { btn.isEnabled = true; if (ok) { etU.text.clear(); etP.text.clear(); refreshList() }; Toast.makeText(act, msg, Toast.LENGTH_SHORT).show() }
                }.start()
            }
        })
        val etReset = EditText(act).apply { hint = "Username untuk reset HWID (ganti HP)"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY); setPadding(0, 24, 0, 12) }
        card.addView(etReset)
        card.addView(Button(act).apply {
            text = "RESET HWID AKUN (ONLINE)"; background = box("#0E0F14", "#00D2FF", 16f); setTextColor(Color.WHITE)
            setOnClickListener {
                val u = etReset.text.toString().trim().lowercase()
                if (u.isBlank()) { Toast.makeText(act, "Isi username dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                act.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE).edit().remove("hwid_$u").apply()
                Thread {
                    var ok = true
                    if (AuthManager.DB_URL.isNotBlank()) {
                        try {
                            val put = (URL("${AuthManager.DB_URL.trimEnd('/')}/accounts/$u/hwid.json").openConnection() as HttpURLConnection).apply {
                                requestMethod = "PUT"; doOutput = true; setRequestProperty("Content-Type", "application/json")
                            }
                            put.outputStream.use { it.write("\"\"".toByteArray()) }; ok = put.responseCode in 200..299
                        } catch (_: Exception) { ok = false }
                    }
                    act.runOnUiThread { Toast.makeText(act, if (ok) "HWID $u direset! Bisa login di HP baru." else "Gagal reset online", Toast.LENGTH_SHORT).show() }
                }.start()
            }
        })
        parent.addView(card)
    }
}
