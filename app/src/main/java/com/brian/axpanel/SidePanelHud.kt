package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable
import android.media.AudioManager; import android.net.wifi.WifiManager; import android.os.Build; import android.view.*; import android.widget.*
object SidePanelHud {
    private var animOff = false; private var ptrIdx = 0; private var isRec = false
    private var wifiLock: WifiManager.WifiLock? = null
    private fun box(bg: String, stroke: String, r: Float = 18f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    fun build(ctx: Context, onCollapse: () -> Unit, onStopAll: () -> Unit, onToast: (String) -> Unit): Pair<FrameLayout, TextView> {
        val wrap = FrameLayout(ctx).apply { background = box("#EB0B070B", "#D4AF37", 28f) }; wrap.addView(ImageView(ctx).apply { setImageResource(R.drawable.skeleton_king); alpha = 0.26f; scaleType = ImageView.ScaleType.CENTER_CROP; layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT) }); val hud = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 16, 24, 16) }
        val top = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, 0, 0, 10) }
        val tvStats = TextView(ctx).apply { text = "YAN TOOLS • --:-- • RAM --%"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
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
        c1.addView(b("Animasi 0x") {
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            animOff = !animOff; val v = if (animOff) "0.0" else "1.0"
            Thread { val ok = ShizukuHelper.runCommand("settings put global window_animation_scale $v && settings put global transition_animation_scale $v"); onToast(if (ok) (if (animOff) "Animasi 0x Aktif!" else "Animasi Normal 1x") else "Gagal!") }.start()
        })
        c1.addView(b("Reset Aman") {
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            Thread { ShizukuHelper.runCommand("wm size reset; wm density reset; cmd power set-fixed-performance-mode-enabled false; settings put system screen_brightness_mode 1") }.start()
            onToast("Setelan Layar & Power Direset!")
        })
        c1.addView(b("Pointer") {
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            val vals = listOf(0, 4, 7); ptrIdx = (ptrIdx + 1) % 3
            Thread { val ok = ShizukuHelper.runCommand("settings put system pointer_speed ${vals[ptrIdx]}"); onToast(if (ok) "Pointer Speed: +${vals[ptrIdx]}" else "Gagal ubah pointer") }.start()
        })
        c2.addView(b("Crosshair") { val i = Intent(ctx, CrosshairOverlayService::class.java); if (CrosshairOverlayService.isRunning) ctx.stopService(i) else if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(i) else ctx.startService(i) })
        c2.addView(b("Monitor") { val i = Intent(ctx, MonitoringOverlayService::class.java); if (MonitoringOverlayService.isRunning) ctx.stopService(i) else if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(i) else ctx.startService(i) })
        c2.addView(b("WiFi Lock") { btn ->
            val wm = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiLock == null) {
                @Suppress("DEPRECATION") val m = if (Build.VERSION.SDK_INT >= 29) WifiManager.WIFI_MODE_FULL_LOW_LATENCY else WifiManager.WIFI_MODE_FULL_HIGH_PERF
                wifiLock = wm.createWifiLock(m, "YM_HUD").apply { acquire() }
                if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("settings put global wifi_sleep_policy 2; settings put global wifi_scan_always_enabled 0") }.start()
                btn.text = "WiFi: ON"; onToast("WiFi Lock Aktif!")
            } else {
                wifiLock?.let { if (it.isHeld) it.release() }; wifiLock = null
                btn.text = "WiFi Lock"; onToast("WiFi Lock Dimatikan")
            }
        })
        c3.addView(b("Turbo Boost") {
            if (!ShizukuHelper.hasShizukuPermission()) onToast("Hubungkan Shizuku dulu!")
            else Thread {
                val ok = ShizukuHelper.runCommand("cmd power set-fixed-performance-mode-enabled true && settings put global cached_apps_freezer enabled && settings put global wifi_scan_always_enabled 0")
                onToast(if (ok) "Turbo Boost Aktif!" else "Turbo Boost Gagal!")
            }.start()
        })
        c3.addView(b("Rekam 20m") { btn ->
            if (!ShizukuHelper.hasShizukuPermission()) { onToast("Hubungkan Shizuku dulu!"); return@b }
            if (!isRec) {
                isRec = true; btn.text = "Stop Rekam"
                val ts = System.currentTimeMillis()
                val cmd = "mkdir -p /sdcard/Movies && touch /sdcard/Movies/.ym_rec && i=1; while [ -f /sdcard/Movies/.ym_rec ] && [ ${'$'}i -le 7 ]; do f=/sdcard/Movies/YM_${ts}_part${'$'}i.mp4; screenrecord --time-limit 180 ${'$'}f; am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://${'$'}f; i=${'$'}((i+1)); done; rm -f /sdcard/Movies/.ym_rec"
                Thread { ShizukuHelper.runCommand(cmd) }.start()
                onToast("Rekam dimulai (hingga 21 menit)!")
            } else {
                isRec = false; btn.text = "Rekam 20m"
                Thread { ShizukuHelper.runCommand("rm -f /sdcard/Movies/.ym_rec && pkill -2 screenrecord") }.start()
                onToast("Video disimpan di folder Movies!")
            }
        })
        c3.addView(b("Screenshot") {
            if (!ShizukuHelper.hasShizukuPermission()) onToast("Hubungkan Shizuku dulu!")
            else Thread {
                val f = "/sdcard/Pictures/YM_${System.currentTimeMillis()}.png"
                val ok = ShizukuHelper.runCommand("mkdir -p /sdcard/Pictures && screencap -p $f && am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://$f")
                onToast(if (ok) "Screenshot tersimpan di Pictures!" else "Gagal screenshot!")
            }.start()
        })
        c4.addView(b("Ke Aplikasi") { ctx.startActivity(Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); onCollapse() })
        c4.addView(b("Refresh Hz") { @Suppress("DEPRECATION") onToast("Layar aktif: ${(ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.refreshRate.toInt()} Hz") })
        c4.addView(b("Matikan") { wifiLock?.let { if (it.isHeld) it.release() }; wifiLock = null; onStopAll() })
        grid.addView(c1); grid.addView(c2); grid.addView(c3); grid.addView(c4); hud.addView(grid)
        val sliders = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 10, 0, 0); gravity = Gravity.CENTER_VERTICAL }
        val seekBri = SeekBar(ctx).apply {
            max = 255; progress = 150; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) {}
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {
                    val v = (s?.progress ?: 150).coerceAtLeast(15)
                    if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("settings put system screen_brightness_mode 0 && settings put system screen_brightness $v") }.start()
                    else onToast("Hubungkan Shizuku untuk atur kecerahan!")
                }
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
        hud.addView(sliders); wrap.addView(hud); return wrap to tvStats
    }
}
