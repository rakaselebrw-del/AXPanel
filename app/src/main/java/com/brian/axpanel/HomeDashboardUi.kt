package com.brian.axpanel
import android.app.ActivityManager; import android.content.Context; import android.content.Intent; import android.content.IntentFilter
import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable
import android.net.Uri; import android.os.*; import android.provider.Settings; import android.view.*; import android.widget.*
object HomeDashboardUi {
    private fun grad(c1: String, c2: String, stroke: String, r: Float = 28f) = GradientDrawable(
        GradientDrawable.Orientation.TL_BR, intArrayOf(Color.parseColor(c1), Color.parseColor(c2))
    ).apply { cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    fun build(ctx: Context): ScrollView {
        val scroll = ScrollView(ctx)
        val root = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setPadding(36, 24, 36, 28) }
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
        val usedMb = (mi.totalMem - mi.availMem) / (1024 * 1024); val totalMb = mi.totalMem / (1024 * 1024)
        val ramPct = (((mi.totalMem - mi.availMem) * 100) / mi.totalMem).toInt()
        val bat = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = (bat?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
        val hero = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = grad("#2E0810", "#130A0D", "#E50914", 30f); setPadding(36, 32, 36, 32)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 24) }
        }
        hero.addView(TextView(ctx).apply { text = "YM GAMING ENGINE • STATUS"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF4D57")) })
        hero.addView(TextView(ctx).apply { text = "${Build.MODEL}  •  Suhu ${temp}°C"; textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); setPadding(0, 6, 0, 12) })
        hero.addView(TextView(ctx).apply { text = "RAM Terpakai: $usedMb / $totalMb MB ($ramPct%)"; textSize = 12f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 8) })
        hero.addView(ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100; progress = ramPct; progressTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E50914")) })
        hero.addView(TextView(ctx).apply {
            text = if (ShizukuHelper.hasShizukuPermission()) "● Shizuku Terhubung (Siap Boost)" else "○ Shizuku Belum Aktif (Buka tab Developer)"
            textSize = 11f; setTextColor(if (ShizukuHelper.hasShizukuPermission()) Color.parseColor("#00E676") else Color.parseColor("#FFB300")); setPadding(0, 14, 0, 0)
        })
        root.addView(hero)
        root.addView(TextView(ctx).apply { text = "AKSI CEPAT GAMING"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#8E92A4")); setPadding(4, 0, 0, 12) })
        fun qBtn(t: String, c1: String, f: () -> Unit) = Button(ctx).apply {
            text = t; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = grad(c1, "#120B0E", "#3D151B", 22f)
            layoutParams = LinearLayout.LayoutParams(0, 130, 1f).apply { setMargins(8, 8, 8, 8) }; setOnClickListener { f() }
        }
        val r1 = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        r1.addView(qBtn("GAME TURBO HUD", "#3A0A12") {
            if (!Settings.canDrawOverlays(ctx)) { ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))); Toast.makeText(ctx, "Izinkan tampilkan di atas aplikasi", Toast.LENGTH_LONG).show() }
            else { val s = Intent(ctx, SidePanelService::class.java); if (SidePanelService.isRunning) ctx.stopService(s) else if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(s) else ctx.startService(s) }
        })
        r1.addView(qBtn("ROG 9 MODULE", "#3A0A12") {
            if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
            else { Thread { ShizukuHelper.runCommand("cmd game mode performance com.dts.freefireth; cmd game mode performance com.dts.freefiremax; cmd power set-fixed-performance-mode-enabled true; pm trim-caches 1000M") }.start(); Toast.makeText(ctx, "ROG 9 Mode Aktif!", Toast.LENGTH_SHORT).show() }
        })
        val r2 = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 0, 0, 20) }
        r2.addView(qBtn("MODE RINGAN 720p", "#1D1014") {
            if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
            else { Thread { ShizukuHelper.runCommand("wm size 720x1600 && settings put global window_animation_scale 0.0") }.start(); Toast.makeText(ctx, "Mode Ringan 720p Aktif!", Toast.LENGTH_SHORT).show() }
        })
        r2.addView(qBtn("RESET NORMAL", "#14151C") {
            if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
            else { Thread { ShizukuHelper.runCommand("wm size reset && settings put global window_animation_scale 1.0") }.start(); Toast.makeText(ctx, "Resolusi & Animasi Normal!", Toast.LENGTH_SHORT).show() }
        })
        root.addView(r1); root.addView(r2)
        root.addView(TextView(ctx).apply { text = "LUNCURKAN GAME"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#8E92A4")); setPadding(4, 8, 0, 12) })
        listOf("Free Fire" to "com.dts.freefireth", "Free Fire MAX" to "com.dts.freefiremax").forEach { (t, pkg) ->
            val card = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; background = grad("#1B1014", "#120E12", "#2D181E", 26f); setPadding(32, 28, 32, 28)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 18) }
            }
            val col = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
            col.addView(TextView(ctx).apply { text = t; textSize = 17f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE) })
            col.addView(TextView(ctx).apply { text = pkg; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")) })
            card.addView(col)
            card.addView(Button(ctx).apply {
                text = "MAIN"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = grad("#E50914", "#B0040E", "#FF3B30", 18f)
                setOnClickListener {
                    if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("pm trim-caches 500M && am kill-all") }.start()
                    val i = ctx.packageManager.getLaunchIntentForPackage(pkg); if (i != null) ctx.startActivity(i) else Toast.makeText(ctx, "$t belum terinstal", Toast.LENGTH_SHORT).show()
                }
            })
            root.addView(card)
        }
        scroll.addView(root); return scroll
    }
}
