package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable
import android.media.AudioManager; import android.net.wifi.WifiManager; import android.os.Build; import android.view.*; import android.widget.*
object SidePanelHud {
    private var resIdx = 0; private var lightIdx = 0; private var ptrIdx = 0; private var isRec = false
    private fun box(bg: String, stroke: String, r: Float = 18f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    fun build(ctx: Context, onCollapse: () -> Unit, onStopAll: () -> Unit, onToast: (String) -> Unit): Pair<LinearLayout, TextView> {
        val hud = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; background = box("#E60D080A", "#FF1E27", 28f); setPadding(24, 16, 24, 16) }
        val top = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, 0, 0, 10) }
        val tvStats = TextView(ctx).apply { text = "YM HUD • --:-- • RAM --%"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
        val btnClose = Button(ctx).apply { text = "X"; textSize = 11f; setTextColor(Color.WHITE); background = box("#D90416", "#FF3B30", 14f); layoutParams = LinearLayout.LayoutParams(80, 70); setOnClickListener { onCollapse() } }
        top.addView(tvStats); top.addView(btnClose); hud.addView(top)
        val grid = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        fun col() = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(4, 0, 4, 0) } }
        fun b(t: String, f: (Button) -> Unit) = Button(ctx).apply {
            text = t; textSize = 10f; setTextColor(Color.WHITE); background = box("#1A0B0E", "#3D151B", 14f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 84).apply { setMargins(0, 4, 0, 4) }
            setOnClickListener { f(this) }
        }
        val c1 = col(); val c2 = col(); val c3 = col(); val c4 = col()
        c1.addView(b("Mode Ringan") {
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            lightIdx = (lightIdx + 1) % 3
            val cmd = when (lightIdx) { 1 -> "wm size 720x1600 && settings put global window_animation_scale 0.0"; 2 -> "wm size 540x1200 && settings put global window_animation_scale 0.0"; else -> "wm size reset && settings put global window_animation_scale 1.0" }
            Thread { ShizukuHelper.runCommand(cmd) }.start(); onToast(listOf("Layar Asli", "Ringan 720p", "Super Ringan 540p")[lightIdx])
        })
        c1.addView(b("Resolusi") {
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            resIdx = (resIdx + 1) % 3
            val cmd = when (resIdx) { 1 -> "wm size 1080x2400"; 2 -> "wm size 1320x2868"; else -> "wm size reset" }
            Thread { ShizukuHelper.runCommand(cmd) }.start(); onToast(listOf("Resolusi Normal", "ROG 9 Pro", "iPhone PM")[resIdx])
        })
        c1.addView(b("Pointer") {
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            val vals = listOf(0, 4, 7); ptrIdx = (ptrIdx + 1) % 3
            Thread { ShizukuHelper.runCommand("settings put system pointer_speed ${vals[ptrIdx]}") }.start(); onToast("Pointer Speed: +${vals[ptrIdx]}")
        })
        c2.addView(b("Crosshair") { val i = Intent(ctx, CrosshairOverlayService::class.java); if (CrosshairOverlayService.isRunning) ctx.stopService(i) else if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(i) else ctx.startService(i) })
        c2.addView(b("Monitor") { val i = Intent(ctx, MonitoringOverlayService::class.java); if (MonitoringOverlayService.isRunning) ctx.stopService(i) else if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(i) else ctx.startService(i) })
        c2.addView(b("WiFi Lock") {
            val wm = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION") wm.createWifiLock(if (Build.VERSION.SDK_INT >= 29) WifiManager.WIFI_MODE_FULL_LOW_LATENCY else WifiManager.WIFI_MODE_FULL_HIGH_PERF, "YM_HUD").acquire()
            if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("settings put global wifi_sleep_policy 2") }.start()
            onToast("WiFi Stabilizer Aktif!")
        })
        c3.addView(b("Turbo Boost") {
            if (!ShizukuHelper.hasShizukuPermission()) onToast("Hubungkan Shizuku dulu!")
            else { Thread { ShizukuHelper.runCommand("pm trim-caches 500M && am kill-all && cmd power set-fixed-performance-mode-enabled true") }.start(); onToast("Turbo Boost Aktif!") }
        })
        c3.addView(b("Rekam") { btn ->
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            if (!isRec) { isRec = true; btn.text = "Stop Rekam"; Thread { ShizukuHelper.runCommand("mkdir -p /sdcard/Movies && nohup screenrecord /sdcard/Movies/YM_${System.currentTimeMillis()}.mp4 >/dev/null 2>&1 &") }.start(); onToast("Rekam dimulai!") }
            else { isRec = false; btn.text = "Rekam"; Thread { ShizukuHelper.runCommand("pkill -2 screenrecord") }.start(); onToast("Video disimpan di Movies!") }
        })
        c3.addView(b("Screenshot") {
            if (!ShizukuHelper.hasShizukuPermission()) onToast("Hubungkan Shizuku dulu!")
            else { Thread { ShizukuHelper.runCommand("mkdir -p /sdcard/Pictures && screencap -p /sdcard/Pictures/YM_${System.currentTimeMillis()}.png") }.start(); onToast("Screenshot disimpan!") }
        })
        c4.addView(b("Ke Aplikasi") { ctx.startActivity(Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); onCollapse() })
        c4.addView(b("Refresh Hz") { onToast("Layar aktif: ${(ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.refreshRate.toInt()} Hz") })
        c4.addView(b("Matikan") { onStopAll() })
        grid.addView(c1); grid.addView(c2); grid.addView(c3); grid.addView(c4); hud.addView(grid)
        val sliders = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 10, 0, 0); gravity = Gravity.CENTER_VERTICAL }
        val seekBri = SeekBar(ctx).apply {
            max = 255; progress = 150; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {}
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) { if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("settings put system screen_brightness ${s?.progress ?: 150}") }.start() }
            })
        }
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val seekVol = SeekBar(ctx).apply {
            max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); progress = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { if (f) am.setStreamVolume(AudioManager.STREAM_MUSIC, p, 0) }
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        sliders.addView(TextView(ctx).apply { text = "☀"; setTextColor(Color.WHITE) }); sliders.addView(seekBri)
        sliders.addView(TextView(ctx).apply { text = "🔊"; setTextColor(Color.WHITE); setPadding(16, 0, 0, 0) }); sliders.addView(seekVol)
        hud.addView(sliders); return hud to tvStats
    }
}
