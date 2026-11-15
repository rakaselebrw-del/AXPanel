package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable; import android.widget.*
object PhoneModulesUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "MODULE PERFORMA HP FLAGSHIP (SHIZUKU)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Terapkan profil performa, refresh rate & Android Game Mode agar HP lebih lancar."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        fun modBtn(title: String, bg: String, cmd: String, msg: String) = Button(ctx).apply {
            text = title; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand(cmd) }.start(); Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
            }
        }
        val ffCmd = "cmd game mode performance com.dts.freefireth; cmd game mode performance com.dts.freefiremax"
        card.addView(modBtn("Module ROG Phone 9 Pro (Max FPS)", "#E50914", "$ffCmd; cmd power set-fixed-performance-mode-enabled true; settings put system peak_refresh_rate 120.0; settings put global window_animation_scale 0.0; settings put global transition_animation_scale 0.0; pm trim-caches 1000M; am kill-all", "Module ROG 9 Pro Aktif!"))
        card.addView(modBtn("Module RedMagic 10 Pro (Cooling/Boost)", "#2A0A10", "$ffCmd; cmd power set-fixed-performance-mode-enabled true; settings put global wifi_sleep_policy 2; settings put global animator_duration_scale 0.0; am kill-all", "Module RedMagic 10 Pro Aktif!"))
        card.addView(modBtn("Module iQOO 13 Esports (Touch + Speed)", "#2A0A10", "$ffCmd; settings put system pointer_speed 4; settings put system peak_refresh_rate 120.0; pm trim-caches 500M", "Module iQOO 13 Esports Aktif!"))
        card.addView(modBtn("Module Poco / Xiaomi Turbo (Ringan)", "#2A0A10", "$ffCmd; settings put global window_animation_scale 0.5; settings put global transition_animation_scale 0.5; pm trim-caches 500M; am kill-all", "Module Poco Turbo Aktif!"))
        card.addView(modBtn("Reset Module ke Standar Pabrik", "#0E0F14", "cmd game mode standard com.dts.freefireth; cmd game mode standard com.dts.freefiremax; cmd power set-fixed-performance-mode-enabled false; settings put global window_animation_scale 1.0; settings put global transition_animation_scale 1.0; settings put global animator_duration_scale 1.0; settings put system pointer_speed 0", "Semua Module di-reset ke Standar!"))
        parent.addView(card)
    }
}
