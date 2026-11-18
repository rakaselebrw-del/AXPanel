package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.net.Uri; import android.net.wifi.WifiManager
import android.os.Build; import android.provider.Settings; import android.widget.*
object FeatureTabsUi {
    private var subTab = 0; private var wifiLock: WifiManager.WifiLock? = null
    private fun box(bg: String, st: String, r: Float = 24f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st)) }
    fun build(ctx: Context): LinearLayout {
        val root = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.TRANSPARENT) }
        val hScroll = HorizontalScrollView(ctx).apply { isHorizontalScrollBarEnabled = false; setPadding(28, 20, 28, 12) }
        val tabBar = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        val bodyWrap = ScrollView(ctx).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val body = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setPadding(36, 12, 36, 32) }
        bodyWrap.addView(body)
        val titles = listOf("⚡ Boost", "📦 Module Tambahan", "📱 Layar", "🎯 Overlay", "🎮 Lab & Fun")
        val btns = mutableListOf<Button>()
        fun renderBody() {
            body.removeAllViews()
            btns.forEachIndexed { i, b ->
                b.background = if (i == subTab) box("#E50914", "#FF3B30", 40f) else box("#15161E", "#262834", 40f)
                b.setTextColor(if (i == subTab) Color.WHITE else Color.parseColor("#8E92A4"))
            }
            fun overlaySw(title: String, running: Boolean, cls: Class<*>) = Switch(ctx).apply {
                text = title; setTextColor(Color.WHITE); isChecked = running; setPadding(0, 14, 0, 14)
                setOnCheckedChangeListener { bv, chk ->
                    if (chk) {
                        if (!Settings.canDrawOverlays(ctx)) { bv.isChecked = false; ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${ctx.packageName}"))); Toast.makeText(ctx, "Izinkan tampilkan di atas aplikasi lain", Toast.LENGTH_LONG).show() }
                        else { val svc = Intent(ctx, cls); if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(svc) else ctx.startService(svc) }
                    } else ctx.stopService(Intent(ctx, cls))
                }
            }
            fun updCrosshair() { if (CrosshairOverlayService.isRunning) ctx.startService(Intent(ctx, CrosshairOverlayService::class.java).apply { action = CrosshairOverlayService.ACTION_UPDATE }) }
            when (subTab) {
                0 -> {
                    val c1 = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834"); setPadding(32, 24, 32, 24); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) } }
                    c1.addView(overlaySw("Game Panel Samping (HUD Melayang)", SidePanelService.isRunning, SidePanelService::class.java))
                    c1.addView(Switch(ctx).apply {
                        text = "WiFi Stabilizer (Low Latency)"; setTextColor(Color.WHITE); isChecked = wifiLock != null; setPadding(0, 12, 0, 12)
                        setOnCheckedChangeListener { _, chk ->
                            val wmWifi = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                            if (chk) {
                                @Suppress("DEPRECATION") val mode = if (Build.VERSION.SDK_INT >= 29) WifiManager.WIFI_MODE_FULL_LOW_LATENCY else WifiManager.WIFI_MODE_FULL_HIGH_PERF
                                wifiLock = wmWifi.createWifiLock(mode, "YM_WifiLock").apply { acquire() }
                                if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("settings put global wifi_sleep_policy 2") }.start()
                                Toast.makeText(ctx, "WiFi Stabilizer Aktif!", Toast.LENGTH_SHORT).show()
                            } else { wifiLock?.release(); wifiLock = null; Toast.makeText(ctx, "WiFi Stabilizer Nonaktif", Toast.LENGTH_SHORT).show() }
                        }
                    })
                    body.addView(c1); ExtraToolsUi.build(ctx, body)
                }
                1 -> { PhoneModulesUi.build(ctx, body); TsxModuleUi.build(ctx, body); GoodDataModuleUi.build(ctx, body); AngleDriverUi.build(ctx, body); SorenModuleUi.build(ctx, body); CustomScriptUi.build(ctx, body) }
                2 -> {
                    val c2 = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834"); setPadding(32, 24, 32, 24); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) } }
                    c2.addView(TextView(ctx).apply { text = "RESOLUSI LAYAR (SHIZUKU)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 12) })
                    fun resBtn(l: String, cmd: String) = Button(ctx).apply {
                        text = l; textSize = 12f; setTextColor(Color.WHITE); background = box("#0E0F14", "#2D181E", 16f)
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
                        setOnClickListener { if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show() else { Thread { ShizukuHelper.runCommand(cmd) }.start(); Toast.makeText(ctx, "Diterapkan: $l", Toast.LENGTH_SHORT).show() } }
                    }
                    c2.addView(resBtn("ROG Phone 9 Pro (1080x2400)", "wm size 1080x2400"))
                    c2.addView(resBtn("iPhone 17/18 Pro Max (1320x2868)", "wm size 1320x2868"))
                    c2.addView(resBtn("Reset Resolusi Default (Aman)", "wm size reset && wm density reset"))
                    body.addView(c2); LightModeUi.build(ctx, body); RefreshRateUi.build(ctx, body); TouchDpiUi.build(ctx, body); SensiSmartUi.build(ctx, body)
                }
                3 -> {
                    val c = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834"); setPadding(32, 24, 32, 24); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) } }
                    c.addView(overlaySw("Monitoring Performa (RAM/Suhu/Hz)", MonitoringOverlayService.isRunning, MonitoringOverlayService::class.java))
                    c.addView(overlaySw("Crosshair di Game", CrosshairOverlayService.isRunning, CrosshairOverlayService::class.java))
                    val tvSize = TextView(ctx).apply { text = "Size Crosshair: ${CrosshairOverlayService.crosshairSize}%"; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 16, 0, 4) }; c.addView(tvSize)
                    c.addView(SeekBar(ctx).apply { max = 100; progress = CrosshairOverlayService.crosshairSize; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { val v = if (p < 10) 10 else p; CrosshairOverlayService.crosshairSize = v; tvSize.text = "Size Crosshair: $v%"; updCrosshair() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
                    val tvPos = TextView(ctx).apply { text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 16, 0, 4) }; c.addView(tvPos)
                    c.addView(SeekBar(ctx).apply { max = 400; progress = CrosshairOverlayService.offsetX + 200; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { CrosshairOverlayService.offsetX = p - 200; tvPos.text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; updCrosshair() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
                    c.addView(SeekBar(ctx).apply { max = 400; progress = CrosshairOverlayService.offsetY + 200; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { CrosshairOverlayService.offsetY = p - 200; tvPos.text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; updCrosshair() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
                    c.addView(TextView(ctx).apply { text = "Warna Crosshair"; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 16, 0, 8) })
                    val colorRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
                    listOf(Color.RED, Color.GREEN, Color.YELLOW, Color.WHITE, Color.CYAN, Color.BLACK).forEach { col -> colorRow.addView(Button(ctx).apply { setBackgroundColor(col); layoutParams = LinearLayout.LayoutParams(0, 86, 1f).apply { setMargins(8, 0, 8, 0) }; setOnClickListener { CrosshairOverlayService.crosshairColor = col; updCrosshair() } }) }
                    c.addView(colorRow); CrosshairStyleUi.build(ctx, c); body.addView(c); FinalFeaturesUi.build(ctx, body)
                }
                4 -> { ProGamingToolsUi.build(ctx, body); FunPackUi.build(ctx, body) }
            }
        }
        titles.forEachIndexed { idx, t ->
            val b = Button(ctx).apply {
                text = t; textSize = 12f; isAllCaps = false; setPadding(36, 12, 36, 12)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 92).apply { setMargins(0, 0, 14, 0) }
                setOnClickListener { subTab = idx; renderBody() }
            }
            btns.add(b); tabBar.addView(b)
        }
        hScroll.addView(tabBar); root.addView(hScroll); root.addView(bodyWrap); renderBody(); return root
    }
}
