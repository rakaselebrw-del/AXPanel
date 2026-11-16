package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.*; import android.widget.*
object CustomScriptUi {
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "CUSTOM SCRIPT RUNNER (SHIZUKU)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Jalankan file .sh sistem aman yang sudah diekstrak ke memori internal (/sdcard/)."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 12) })
        val etPath = EditText(ctx).apply {
            setText("/sdcard/GOOD_DATA/run.sh"); hint = "Contoh: /sdcard/NamaFolder/run.sh"
            setTextColor(Color.WHITE); setHintTextColor(Color.GRAY); background = box("#0E0F14", "#2D181E", 16f); setPadding(24, 18, 24, 18)
        }
        card.addView(etPath)
        card.addView(Button(ctx).apply {
            text = "▶ JALANKAN SKRIP VIA SHIZUKU"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 14, 0, 0) }
            setOnClickListener {
                val p = etPath.text.toString().trim()
                if (p.isBlank()) { Toast.makeText(ctx, "Isi path file .sh dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                Toast.makeText(ctx, "Menjalankan skrip...", Toast.LENGTH_SHORT).show()
                Thread {
                    val ok = ShizukuHelper.runCommand("[ -f \"$p\" ] && sh \"$p\"")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(ctx, if (ok) "Skrip selesai dijalankan!" else "File tidak ditemukan atau gagal: $p", Toast.LENGTH_LONG).show()
                    }
                }.start()
            }
        })
        parent.addView(card)
    }
}
