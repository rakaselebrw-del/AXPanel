package com.brian.axpanel
import android.content.*; import android.graphics.*; import android.graphics.drawable.GradientDrawable
import android.net.Uri; import android.os.Build; import android.provider.Settings; import android.widget.*
object FinalFeaturesUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "FITUR FINAL (TIMER & CATATAN HUD)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 10) })
        card.addView(Switch(ctx).apply {
            text = "Floating Timer / Stopwatch (Tap=Play, Tahan=Reset)"; setTextColor(Color.WHITE); isChecked = TimerOverlayService.isRunning; setPadding(0, 8, 0, 16)
            setOnCheckedChangeListener { bv, chk ->
                if (chk) {
                    if (!Settings.canDrawOverlays(ctx)) { bv.isChecked = false; ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))) }
                    else { val s = Intent(ctx, TimerOverlayService::class.java); if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(s) else ctx.startService(s) }
                } else ctx.stopService(Intent(ctx, TimerOverlayService::class.java))
            }
        })
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val etNote = EditText(ctx).apply {
            hint = "Simpan kode HUD / catatan sensi di sini..."; setText(prefs.getString("hud_notes", ""))
            setTextColor(Color.WHITE); setHintTextColor(Color.GRAY); background = box("#0E0F14", "#262834", 16f); setPadding(24, 18, 24, 18)
        }
        card.addView(etNote)
        val row = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 12, 0, 12) }
        row.addView(Button(ctx).apply {
            text = "SIMPAN CATATAN"; textSize = 11f; setTextColor(Color.WHITE); background = box("#2A0A10", "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(0, 0, 8, 0) }
            setOnClickListener { prefs.edit().putString("hud_notes", etNote.text.toString()).apply(); Toast.makeText(ctx, "Catatan disimpan!", Toast.LENGTH_SHORT).show() }
        })
        row.addView(Button(ctx).apply {
            text = "SALIN"; textSize = 11f; setTextColor(Color.WHITE); background = box("#0E0F14", "#00D2FF", 16f)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(8, 0, 0, 0) }
            setOnClickListener {
                (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("YMNote", etNote.text.toString()))
                Toast.makeText(ctx, "Disalin!", Toast.LENGTH_SHORT).show()
            }
        })
        card.addView(row)
        card.addView(Button(ctx).apply {
            text = "RESET AMAN SEMUA SETELAN SISTEM"; textSize = 12f; setTextColor(Color.WHITE); background = box("#0E0F14", "#262834", 16f)
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
                else {
                    Thread { ShizukuHelper.runCommand("wm size reset; wm density reset; settings put global window_animation_scale 1.0; settings put global transition_animation_scale 1.0; settings put global animator_duration_scale 1.0; settings put system pointer_speed 0; settings put secure long_press_timeout 400; cmd power set-fixed-performance-mode-enabled false") }.start()
                    Toast.makeText(ctx, "Semua setelan kembali ke Normal!", Toast.LENGTH_SHORT).show()
                }
            }
        })
        card.addView(TextView(ctx).apply { text = "\nYM Gaming Panel • Versi 5.0 FINAL"; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")) })
        parent.addView(card)
    }
}
