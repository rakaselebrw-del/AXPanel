package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.widget.*
object ProGamingToolsUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "PRO GAMING SUITE (v6.0)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 10) })
        fun shizukuSw(title: String, cmdOn: String, cmdOff: String) = Switch(ctx).apply {
            text = title; setTextColor(Color.WHITE); setPadding(0, 12, 0, 12)
            setOnCheckedChangeListener { bv, chk ->
                if (!ShizukuHelper.hasShizukuPermission()) { bv.isChecked = false; Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show() }
                else { Thread { ShizukuHelper.runCommand(if (chk) cmdOn else cmdOff) }.start(); Toast.makeText(ctx, "$title: ${if (chk) "AKTIF" else "MATI"}", Toast.LENGTH_SHORT).show() }
            }
        }
        card.addView(shizukuSw("Anti-Notif Pop-up (Mode Fokus)", "settings put global heads_up_notifications_enabled 0", "settings put global heads_up_notifications_enabled 1"))
        card.addView(shizukuSw("Layar Anti-Redup & Kunci Rotasi", "settings put system screen_off_timeout 1800000 && settings put system accelerometer_rotation 0", "settings put system screen_off_timeout 60000 && settings put system accelerometer_rotation 1"))
        card.addView(Button(ctx).apply {
            text = "MODE DINGIN / ANTI-PANAS (60Hz + Kecerahan 45%)"; textSize = 11f; setTextColor(Color.WHITE); background = box("#0E0F14", "#00D2FF", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 12, 0, 10) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand("settings put system peak_refresh_rate 60.0 && settings put system min_refresh_rate 60.0 && settings put system screen_brightness 115 && settings put global window_animation_scale 0.5") }.start(); Toast.makeText(ctx, "Mode Dingin / Anti-Panas Aktif!", Toast.LENGTH_SHORT).show() }
            }
        })
        card.addView(Button(ctx).apply {
            text = "BUKA GAMING LAB (TES REFLEKS & MULTI-TOUCH)"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 16f)
            setOnClickListener { ctx.startActivity(Intent(ctx, GamingLabActivity::class.java)) }
        })
        parent.addView(card)
    }
}
