package com.brian.axpanel
import android.app.ActivityManager; import android.content.*; import android.graphics.*
import android.graphics.drawable.GradientDrawable; import android.net.Uri; import android.os.*
import android.provider.Settings; import android.view.*; import android.widget.*
object HomeDashboardUi {
    private fun grad(c1: String, c2: String, stroke: String, r: Float = 32f) = GradientDrawable(
        GradientDrawable.Orientation.TL_BR, intArrayOf(Color.parseColor(c1), Color.parseColor(c2))
    ).apply { cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    fun build(ctx: Context): FrameLayout {
        val wrap = FrameLayout(ctx)
        wrap.addView(CyberGridView(ctx))
        val scroll = ScrollView(ctx)
        val root = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setPadding(36, 24, 36, 32) }
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
        val usedMb = (mi.totalMem - mi.availMem) / (1024 * 1024); val totalMb = mi.totalMem / (1024 * 1024)
        val ramPct = (((mi.totalMem - mi.availMem) * 100) / mi.totalMem).toInt()
        val bat = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = (bat?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
        @Suppress("DEPRECATION") val hz = (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.refreshRate.toInt()
        val hero = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = grad("#CC1D0B12", "#CC0F1017", "#E50914", 36f); setPadding(32, 28, 32, 28)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 24) }
        }
        hero.addView(TextView(ctx).apply { text = "YAN DELUXION ENGINE • v9.0"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF4D57")) })
        hero.addView(TextView(ctx).apply { text = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}"; textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); setPadding(0, 4, 0, 16) })
        val gauges = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER; setPadding(0, 4, 0, 16) }
        gauges.addView(CircularGaugeView(ctx, ramPct, "RAM", "$ramPct%", "#00D2FF"))
        gauges.addView(CircularGaugeView(ctx, ((temp / 55f) * 100).toInt(), "SUHU", "${temp.toInt()}°C", "#FF9800"))
        gauges.addView(CircularGaugeView(ctx, ((hz / 144f) * 100).toInt(), "LAYAR", "${hz}Hz", "#00E676"))
        hero.addView(gauges)
        hero.addView(TextView(ctx).apply { text = "Memori: $usedMb / $totalMb MB  •  " + if (ShizukuHelper.hasShizukuPermission()) "● Shizuku Aktif" else "○ Shizuku Mati"; textSize = 11f; gravity = Gravity.CENTER; setTextColor(if (ShizukuHelper.hasShizukuPermission()) Color.parseColor("#00E676") else Color.parseColor("#FFB300")) })
        root.addView(hero)
        root.addView(TextView(ctx).apply { text = "KONTROL CEPAT"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#8E92A4")); setPadding(8, 0, 0, 10) })
        fun qBtn(t: String, c1: String, st: String, f: () -> Unit) = Button(ctx).apply {
            text = t; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = grad(c1, "#12131A", st, 26f)
            layoutParams = LinearLayout.LayoutParams(0, 132, 1f).apply { setMargins(8, 8, 8, 8) }; setOnClickListener { f() }
        }
        val r1 = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        r1.addView(qBtn("⚡ GAME HUD", "#2E0C14", "#E50914") {
            if (!Settings.canDrawOverlays(ctx)) { ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))); Toast.makeText(ctx, "Izinkan tampilkan di atas aplikasi", Toast.LENGTH_LONG).show() }
            else { val s = Intent(ctx, SidePanelService::class.java); if (SidePanelService.isRunning) ctx.stopService(s) else if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(s) else ctx.startService(s) }
        })
        r1.addView(qBtn("🚀 ROG 9 MODE", "#2E0C14", "#E50914") {
            if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
            else { Thread { ShizukuHelper.runCommand("cmd power set-fixed-performance-mode-enabled true; settings put global window_animation_scale 0.0") }.start(); Toast.makeText(ctx, "ROG 9 Mode Aktif!", Toast.LENGTH_SHORT).show() }
        })
        val r2 = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 0, 0, 22) }
        r2.addView(qBtn("🎯 AIM ARENA", "#181924", "#00D2FF") { ctx.startActivity(Intent(ctx, AimTrainerActivity::class.java)) })
        r2.addView(qBtn("↺ RESET", "#181924", "#2B2D3D") {
            if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
            else { Thread { ShizukuHelper.runCommand("wm size reset && wm density reset && settings put global window_animation_scale 1.0") }.start(); Toast.makeText(ctx, "Setelan Kembali Normal!", Toast.LENGTH_SHORT).show() }
        })
        root.addView(r1); root.addView(r2)
        root.addView(TextView(ctx).apply { text = "LUNCURKAN GAME"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#8E92A4")); setPadding(8, 4, 0, 10) })
        listOf("Free Fire" to "com.dts.freefireth", "Free Fire MAX" to "com.dts.freefiremax").forEach { (t, pkg) ->
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; background = grad("#CC171822", "#CC101118", "#2B2D3D", 28f); setPadding(32, 26, 32, 26)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 16) }
            }
            val col = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
            col.addView(TextView(ctx).apply { text = t; textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE) })
            col.addView(TextView(ctx).apply { text = pkg; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")) })
            card.addView(col)
            card.addView(Button(ctx).apply {
                text = "MAIN ▶"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = grad("#E50914", "#B0040E", "#FF3B30", 40f)
                setPadding(36, 12, 36, 12)
                setOnClickListener {
                    if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("cmd power set-fixed-performance-mode-enabled true") }.start()
                    ctx.startActivity(Intent(ctx, LaunchGameActivity::class.java).putExtra("pkg", pkg).putExtra("name", t))
                }
            })
            root.addView(card)
        }
        scroll.addView(root); wrap.addView(scroll); return wrap
    }
}
