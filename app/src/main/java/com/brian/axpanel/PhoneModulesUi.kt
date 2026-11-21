package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.*; import android.widget.*
object PhoneModulesUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#CC120E16", "#99FF2A32", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "MODULE PERFORMA & FIX LAG EKSTREM"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Aktifkan sebelum buka game. Aman untuk shader & akun."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        fun modBtn(title: String, bg: String, st: String, cmd: String, msg: String) = Button(ctx).apply {
            text = title; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, st, 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand(cmd) }.start(); Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
            }
        }
        card.addView(modBtn("🔥 MODULE FIX LAG EKSTREM (App Freezer + CPU)", "#E50914", "#FF3B30",
            "cmd power set-fixed-performance-mode-enabled true; settings put global cached_apps_freezer enabled; settings put global wifi_scan_always_enabled 0; settings put global wifi_sleep_policy 2; settings put global window_animation_scale 0.0; settings put global transition_animation_scale 0.0; settings put global animator_duration_scale 0.0",
            "Fix Lag Ekstrem Aktif! Silakan buka game."))
        card.addView(Button(ctx).apply {
            text = "⚡ OPTIMASI ART ENGINE FF & FF MAX (Anti-Stutter)"; textSize = 11f; setTextColor(Color.WHITE); background = box("#2A0A10", "#00D2FF", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                Toast.makeText(ctx, "Mengoptimalkan ART Engine game (tunggu ~10 detik)...", Toast.LENGTH_LONG).show()
                Thread {
                    ShizukuHelper.runCommand("cmd package compile -m speed-profile com.dts.freefireth; cmd package compile -m speed-profile com.dts.freefiremax")
                    Handler(Looper.getMainLooper()).post { Toast.makeText(ctx, "Optimasi ART Engine Selesai!", Toast.LENGTH_SHORT).show() }
                }.start()
            }
        })
        card.addView(modBtn("Module ROG Phone 9 Pro (Max FPS Aman)", "#2A0A10", "#FF3B30", "cmd power set-fixed-performance-mode-enabled true; settings put system peak_refresh_rate 120.0; settings put global window_animation_scale 0.0; settings put global transition_animation_scale 0.0", "Module ROG 9 Pro Aktif!"))
        card.addView(modBtn("Module RedMagic 10 Pro (Power + WiFi)", "#2A0A10", "#FF3B30", "cmd power set-fixed-performance-mode-enabled true; settings put global wifi_sleep_policy 2; settings put global animator_duration_scale 0.0", "Module RedMagic 10 Pro Aktif!"))
        card.addView(modBtn("Module iQOO 13 Esports (Touch + Speed)", "#2A0A10", "#FF3B30", "settings put system pointer_speed 4; settings put system peak_refresh_rate 120.0; settings put secure long_press_timeout 250", "Module iQOO 13 Esports Aktif!"))
        card.addView(modBtn("Reset Semua ke Standar Pabrik", "#0E0F14", "#66D4AF37",
            "wm size reset; wm density reset; cmd power set-fixed-performance-mode-enabled false; settings put global cached_apps_freezer device_default; settings put global wifi_scan_always_enabled 1; settings put global window_animation_scale 1.0; settings put global transition_animation_scale 1.0; settings put global animator_duration_scale 1.0; settings put system pointer_speed 0; settings put secure long_press_timeout 400",
            "Semua Module di-reset ke Standar!"))
        parent.addView(card)
    }
}
