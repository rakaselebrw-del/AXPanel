package com.brian.axpanel
import android.app.ActivityManager; import android.content.*; import android.content.res.ColorStateList
import android.graphics.*; import android.graphics.drawable.GradientDrawable; import android.net.Uri; import android.os.*
import android.provider.Settings; import android.view.*; import android.widget.*
object HomeDashboardUi {
    private fun grad(c1: String, c2: String, stroke: String, r: Float = 32f) = GradientDrawable(
        GradientDrawable.Orientation.TL_BR, intArrayOf(Color.parseColor(c1), Color.parseColor(c2))
    ).apply { cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    fun build(ctx: Context): ScrollView {
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
            orientation = LinearLayout.VERTICAL; background = grad("#231118", "#12131A", "#E50914", 34f); setPadding(36, 32, 36, 32)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 24) }
        }
        hero.addView(TextView(ctx).apply { text = "YM GAMING ENGINE • v7.0"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF4D57")) })
        hero.addView(TextView(ctx).apply { text = Build.MODEL; textSize = 20f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); setPadding(0, 4, 0, 16) })
        val chips = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 0, 0, 18) }
        fun chip(lbl: String, valTxt: String, col: String) = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; background = grad("#171822", "#101118", "#2B2D3D", 22f); setPadding(12, 16, 12, 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(6, 0, 6, 0) }
            addView(TextView(ctx).apply { text = valTxt; textSize = 15f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor(col)) })
            addView(TextView(ctx).apply { text = lbl; textSize = 10f; setTextColor(Color.parseColor("#8E92A4")) })
        }
        chips.addView(chip("RAM", "$ramPct%", "#00D2FF")); chips.addView(chip("SUHU", "${temp}°C", "#FF9800")); chips.addView(chip("LAYAR", "${hz}Hz", "#00E676"))
        hero.addView(chips)
        hero.addView(TextView(ctx).apply { text = "Memori: $usedMb / $totalMb MB"; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 8) })
        hero.addView(ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100; progress = ramPct; progressTintList = ColorStateList.valueOf(Color.parseColor("#E50914"))
            progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A181E"))
        })
        hero.addView(TextView(ctx).apply {
            text = if (ShizukuHelper.hasShizukuPermission()) "● Shizuku Terhubung & Aktif" else "○ Shizuku Belum Aktif (Buka tab Developer)"
            textSize = 11f; setTextColor(if (ShizukuHelper.hasShizukuPermission()) Color.parseColor("#00E676") else Color.parseColor("#FFB300")); setPadding(0, 14, 0, 0)
        })
        root.addView(hero)
        root.addView(TextView(ctx).apply { text = "AKSI CEPAT"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#8E92A4")); setPadding(8, 0, 0, 10) })
        fun qBtn(t: String, c1: String, st: String, f: () -> Unit) = Button(ctx).apply {
            text = t; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = grad(c1, "#12131A", st, 26f)
            layoutParams = LinearLayout.LayoutParams(0, 136, 1f).apply { setMargins(8, 8, 8, 8) }; setOnClickListener { f() }
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
        r2.addView(qBtn("📱 MODE 720p", "#181924", "#2B2D3D") {
            if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
            else { Thread { ShizukuHelper.runCommand("wm size 720x1600 && settings put global window_animation_scale 0.0") }.start(); Toast.makeText(ctx, "Mode Ringan 720p Aktif!", Toast.LENGTH_SHORT).show() }
        })
        r2.addView(qBtn("↺ RESET", "#181924", "#2B2D3D") {
            if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
            else { Thread { ShizukuHelper.runCommand("wm size reset && settings put global window_animation_scale 1.0") }.start(); Toast.makeText(ctx, "Resolusi & Animasi Normal!", Toast.LENGTH_SHORT).show() }
        })
        root.addView(r1); root.addView(r2)
        root.addView(TextView(ctx).apply { text = "LUNCURKAN GAME"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#8E92A4")); setPadding(8, 4, 0, 10) })
        listOf("Free Fire" to "com.dts.freefireth", "Free Fire MAX" to "com.dts.freefiremax").forEach { (t, pkg) ->
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; background = grad("#171822", "#12131A", "#2B2D3D", 28f); setPadding(32, 26, 32, 26)
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
                    val i = ctx.packageManager.getLaunchIntentForPackage(pkg); if (i != null) ctx.startActivity(i) else Toast.makeText(ctx, "$t belum terinstal", Toast.LENGTH_SHORT).show()
                }
            })
            root.addView(card)
        }
        scroll.addView(root); return scroll
    }
}
