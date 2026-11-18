package com.brian.axpanel
import android.content.Context; import android.content.res.ColorStateList
import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.*; import android.widget.*
object SorenModuleUi {
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#D4AF37", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "SOREN HÚSAFELL MODULE"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#D4AF37")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Graphics 0.5x, Power Tuning, Stop Vendor Bloat & Speed Compile FF/FF MAX."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 12) })
        val tvStat = TextView(ctx).apply { text = "Status: Siap"; textSize = 11f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 6) }
        val bar = ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100; progress = 0; progressTintList = ColorStateList.valueOf(Color.parseColor("#E50914"))
            progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#262834"))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 14) }
        }
        card.addView(tvStat); card.addView(bar)
        val bloat = listOf(
            "com.miui.analytics", "com.xiaomi.joyose", "com.miui.daemon", "com.miui.msa.global",
            "com.miui.systemAdSolution", "com.vivo.pushclient", "com.vivo.appstore",
            "com.coloros.oppoguardelf", "com.facebook.services", "com.facebook.appmanager", "com.facebook.system"
        ).joinToString(" ") { "am force-stop $it;" }
        val h = Handler(Looper.getMainLooper())
        card.addView(Button(ctx).apply {
            text = "▶ JALANKAN SORENWARE (FULL BOOST)"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#D4AF37", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 4, 0, 8) }
            setOnClickListener { btn ->
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku di Developer dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                btn.isEnabled = false; bar.progress = 15; tvStat.text = "Menerapkan Graphics, Power & Network..."
                Thread {
                    ShizukuHelper.runCommand("settings put global window_animation_scale 0.5; settings put global transition_animation_scale 0.5; settings put global animator_duration_scale 0.5; settings put global adaptive_battery_management_enabled 0; settings put global app_background_processes 4; settings put global wifi_scan_throttle_enabled 1; settings put global wifi_watchdog_on 0; svc nfc disable")
                    h.post { bar.progress = 45; tvStat.text = "Menghentikan aplikasi bloatware vendor..." }
                    ShizukuHelper.runCommand(bloat)
                    h.post { bar.progress = 70; tvStat.text = "Mengompilasi mesin game (speed)..." }
                    ShizukuHelper.runCommand("cmd package compile -m speed com.dts.freefireth; cmd package compile -m speed com.dts.freefiremax")
                    h.post { bar.progress = 100; tvStat.text = "Status: SØRENWARE Selesai (100%)"; btn.isEnabled = true; Toast.makeText(ctx, "Soren Module Berhasil Diterapkan!", Toast.LENGTH_SHORT).show() }
                }.start()
            }
        })
        card.addView(Button(ctx).apply {
            text = "🛑 STOP BLOATWARE VENDOR SAJA (AMAN)"; textSize = 11f; setTextColor(Color.WHITE); background = box("#2A0A10", "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 4, 0, 8) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                Thread { ShizukuHelper.runCommand(bloat); h.post { bar.progress = 100; tvStat.text = "Bloatware vendor dihentikan!"; Toast.makeText(ctx, "Bloatware dihentikan sementara!", Toast.LENGTH_SHORT).show() } }.start()
            }
        })
        card.addView(Button(ctx).apply {
            text = "↺ REVERT ALL SOREN SETTINGS"; textSize = 11f; setTextColor(Color.WHITE); background = box("#0E0F14", "#262834", 16f)
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                Thread {
                    ShizukuHelper.runCommand("settings put global window_animation_scale 1.0; settings put global transition_animation_scale 1.0; settings put global animator_duration_scale 1.0; settings put global adaptive_battery_management_enabled 1; settings delete global app_background_processes; settings put system screen_brightness_mode 1; settings put global wifi_scan_throttle_enabled 0; settings put global wifi_watchdog_on 1; svc nfc enable")
                    h.post { bar.progress = 0; tvStat.text = "Status: Direset ke Standar"; Toast.makeText(ctx, "Semua setelan Soren dikembalikan!", Toast.LENGTH_SHORT).show() }
                }.start()
            }
        })
        parent.addView(card)
    }
}
