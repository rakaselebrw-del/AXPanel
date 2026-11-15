package com.brian.axpanel
import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable
import android.net.Uri; import android.net.wifi.WifiManager; import android.os.Build; import android.os.Bundle; import android.provider.Settings
import android.view.*; import android.widget.*; import androidx.appcompat.app.AppCompatActivity; import rikka.shizuku.Shizuku
class MainActivity : AppCompatActivity() {
    private lateinit var contentContainer: FrameLayout; private lateinit var tvShizukuStatus: TextView
    private var featureSubTab = 0; private var wifiLock: WifiManager.WifiLock? = null
    private val binderListener = Shizuku.OnBinderReceivedListener { updateShizukuUI() }
    private val permListener = Shizuku.OnRequestPermissionResultListener { _, _ -> updateShizukuUI() }
    private fun box(bg: String, stroke: String, r: Float = 26f) = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = AuthManager.currentUser ?: run { startActivity(Intent(this, LoginActivity::class.java)); finish(); return }
        Shizuku.addBinderReceivedListener(binderListener); Shizuku.addRequestPermissionResultListener(permListener)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0B0709")) }
        val topBar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; background = box("#15161E", "#2D181E", 0f); setPadding(36, 32, 36, 24); gravity = Gravity.CENTER_VERTICAL }
        topBar.addView(ImageView(this).apply { setImageResource(R.drawable.ym_logo); layoutParams = LinearLayout.LayoutParams(92, 92).apply { setMargins(0, 0, 20, 0) } })
        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
        titleCol.addView(TextView(this).apply { text = "YM MODIFIER"; textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")) })
        titleCol.addView(TextView(this).apply { text = "${user.username} • ${AuthManager.getHwid(this@MainActivity)}"; textSize = 11f; setTextColor(Color.parseColor("#00D2FF")) })
        topBar.addView(titleCol)
        topBar.addView(TextView(this).apply { text = user.role.label.uppercase(); textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 18f); setPadding(28, 12, 28, 12) })
        contentContainer = FrameLayout(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val bottomNav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setBackgroundColor(Color.parseColor("#13141B")); setPadding(16, 14, 16, 14) }
        fun navBtn(l: String, f: () -> Unit) = Button(this).apply { text = l; textSize = 13f; setTextColor(Color.WHITE); setBackgroundColor(Color.TRANSPARENT); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); setOnClickListener { f() } }
        bottomNav.addView(navBtn("Home") { showHomeTab() }); bottomNav.addView(navBtn("Feature") { showFeatureTab() }); bottomNav.addView(navBtn("Developer") { showDeveloperTab() })
        root.addView(topBar); root.addView(contentContainer); root.addView(bottomNav); setContentView(root); showHomeTab()
    }
    private fun showHomeTab() {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 28, 40, 24) }
        listOf("Free Fire" to "com.dts.freefireth", "Free Fire MAX" to "com.dts.freefiremax").forEach { (t, pkg) ->
            val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E"); setPadding(36, 36, 36, 36); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 24) } }
            card.addView(TextView(this).apply { text = t; textSize = 18f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE) })
            card.addView(TextView(this).apply { text = pkg; textSize = 12f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 4, 0, 20) })
            card.addView(Button(this).apply { text = "BOOST & BUKA GAME"; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 20f); setOnClickListener {
                if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("pm trim-caches 500M && am kill-all") }.start()
                val i = packageManager.getLaunchIntentForPackage(pkg); if (i != null) startActivity(i) else Toast.makeText(this@MainActivity, "$t belum terinstal", Toast.LENGTH_SHORT).show()
            } })
            layout.addView(card)
        }
        contentContainer.removeAllViews(); contentContainer.addView(layout)
    }
    private fun showFeatureTab() {
        val scroll = ScrollView(this); val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(36, 24, 36, 24) }
        val subRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 0, 0, 24) }
        fun subBtn(l: String, idx: Int) = Button(this).apply {
            text = l; setTextColor(Color.WHITE); background = if (featureSubTab == idx) box("#E50914", "#FF3B30", 20f) else box("#15161E", "#262834", 20f)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(8, 0, 8, 0) }
            setOnClickListener { featureSubTab = idx; showFeatureTab() }
        }
        subRow.addView(subBtn("Tools", 0)); subRow.addView(subBtn("Settings", 1)); layout.addView(subRow)
        if (featureSubTab == 0) {
            fun cardBox() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834"); setPadding(32, 24, 32, 24); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) } }
            val c1 = cardBox()
            fun overlaySw(title: String, running: Boolean, cls: Class<*>) = Switch(this).apply {
                text = title; setTextColor(Color.WHITE); isChecked = running; setPadding(0, 12, 0, 12)
                setOnCheckedChangeListener { bv, chk ->
                    if (chk) {
                        if (!Settings.canDrawOverlays(this@MainActivity)) { bv.isChecked = false; startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))); Toast.makeText(this@MainActivity, "Izinkan tampilkan di atas aplikasi lain", Toast.LENGTH_LONG).show() }
                        else { val svc = Intent(this@MainActivity, cls); if (Build.VERSION.SDK_INT >= 26) startForegroundService(svc) else startService(svc) }
                    } else stopService(Intent(this@MainActivity, cls))
                }
            }
            c1.addView(overlaySw("Game Panel Samping (Foto/Rekam/Boost)", SidePanelService.isRunning, SidePanelService::class.java))
            c1.addView(Switch(this).apply {
                text = "WiFi Stabilizer (Low Latency)"; setTextColor(Color.WHITE); isChecked = wifiLock != null; setPadding(0, 12, 0, 12)
                setOnCheckedChangeListener { _, chk ->
                    val wmWifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                    if (chk) {
                        @Suppress("DEPRECATION") val mode = if (Build.VERSION.SDK_INT >= 29) WifiManager.WIFI_MODE_FULL_LOW_LATENCY else WifiManager.WIFI_MODE_FULL_HIGH_PERF
                        wifiLock = wmWifi.createWifiLock(mode, "YM_WifiLock").apply { acquire() }
                        if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("settings put global wifi_sleep_policy 2") }.start()
                        Toast.makeText(this@MainActivity, "WiFi Stabilizer Aktif!", Toast.LENGTH_SHORT).show()
                    } else { wifiLock?.release(); wifiLock = null; Toast.makeText(this@MainActivity, "WiFi Stabilizer Nonaktif", Toast.LENGTH_SHORT).show() }
                }
            })
            c1.addView(Button(this).apply { text = "JALANKAN GAME BOOSTER"; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 18f); setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(this@MainActivity, "Hubungkan Shizuku di Developer dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand("pm trim-caches 500M && am kill-all && cmd power set-fixed-performance-mode-enabled true") }.start(); Toast.makeText(this@MainActivity, "Booster & Cache Dioptimalkan!", Toast.LENGTH_SHORT).show() }
            } })
            layout.addView(c1)
            val c2 = cardBox()
            c2.addView(TextView(this).apply { text = "RESOLUSI LAYAR (VIA SHIZUKU)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 12) })
            fun resBtn(l: String, cmd: String) = Button(this).apply {
                text = l; textSize = 12f; setTextColor(Color.WHITE); background = box("#0E0F14", "#2D181E", 16f)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
                setOnClickListener {
                    if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(this@MainActivity, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
                    else { Thread { ShizukuHelper.runCommand(cmd) }.start(); Toast.makeText(this@MainActivity, "Resolusi diterapkan: $l", Toast.LENGTH_SHORT).show() }
                }
            }
            c2.addView(resBtn("ROG Phone 9 Pro (1080x2400)", "wm size 1080x2400"))
            c2.addView(resBtn("iPhone 17/18 Pro Max (1320x2868)", "wm size 1320x2868"))
            c2.addView(resBtn("Reset Resolusi Default (Aman)", "wm size reset && wm density reset"))
            layout.addView(c2)
            val c3 = cardBox()
            val tvPtr = TextView(this).apply { text = "Pointer Speed Sistem (Shizuku): 0"; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 8) }
            c3.addView(tvPtr)
            c3.addView(SeekBar(this).apply { max = 14; progress = 7; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { tvPtr.text = "Pointer Speed Sistem: ${p - 7}" }
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {
                    val v = s?.progress?.minus(7) ?: 0
                    if (ShizukuHelper.hasShizukuPermission()) Thread { ShizukuHelper.runCommand("settings put system pointer_speed $v") }.start()
                }
            }) })
            layout.addView(c3)
        } else {
            val c = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834"); setPadding(32, 24, 32, 24) }
            fun overlaySw(title: String, running: Boolean, cls: Class<*>) = Switch(this).apply {
                text = title; setTextColor(Color.WHITE); isChecked = running; setPadding(0, 14, 0, 14)
                setOnCheckedChangeListener { bv, chk ->
                    if (chk) {
                        if (!Settings.canDrawOverlays(this@MainActivity)) { bv.isChecked = false; startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))); Toast.makeText(this@MainActivity, "Izinkan tampilkan di atas aplikasi lain", Toast.LENGTH_LONG).show() }
                        else { val svc = Intent(this@MainActivity, cls); if (Build.VERSION.SDK_INT >= 26) startForegroundService(svc) else startService(svc) }
                    } else stopService(Intent(this@MainActivity, cls))
                }
            }
            c.addView(overlaySw("Monitoring Performa (RAM/Suhu/Hz)", MonitoringOverlayService.isRunning, MonitoringOverlayService::class.java))
            c.addView(overlaySw("Crosshair di Game", CrosshairOverlayService.isRunning, CrosshairOverlayService::class.java))
            val tvSize = TextView(this).apply { text = "Size Crosshair: ${CrosshairOverlayService.crosshairSize}%"; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 20, 0, 4) }; c.addView(tvSize)
            c.addView(SeekBar(this).apply { max = 100; progress = CrosshairOverlayService.crosshairSize; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { val v = if (p < 10) 10 else p; CrosshairOverlayService.crosshairSize = v; tvSize.text = "Size Crosshair: $v%"; updateOverlay() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
            val tvPos = TextView(this).apply { text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 20, 0, 4) }; c.addView(tvPos)
            c.addView(SeekBar(this).apply { max = 400; progress = CrosshairOverlayService.offsetX + 200; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { CrosshairOverlayService.offsetX = p - 200; tvPos.text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; updateOverlay() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
            c.addView(SeekBar(this).apply { max = 400; progress = CrosshairOverlayService.offsetY + 200; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { CrosshairOverlayService.offsetY = p - 200; tvPos.text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; updateOverlay() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
            c.addView(TextView(this).apply { text = "Warna Crosshair"; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 20, 0, 8) })
            val colorRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            listOf(Color.RED, Color.GREEN, Color.YELLOW, Color.WHITE, Color.CYAN, Color.BLACK).forEach { col -> colorRow.addView(Button(this).apply { setBackgroundColor(col); layoutParams = LinearLayout.LayoutParams(0, 90, 1f).apply { setMargins(8, 0, 8, 0) }; setOnClickListener { CrosshairOverlayService.crosshairColor = col; updateOverlay() } }) }
            c.addView(colorRow); CrosshairStyleUi.build(this, c); layout.addView(c)
        }
        scroll.addView(layout); contentContainer.removeAllViews(); contentContainer.addView(scroll)
    }
    private fun updateOverlay() { if (CrosshairOverlayService.isRunning) startService(Intent(this, CrosshairOverlayService::class.java).apply { action = CrosshairOverlayService.ACTION_UPDATE }) }
    private fun showDeveloperTab() {
        val scroll = ScrollView(this); val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(36, 24, 36, 24) }
        val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834"); setPadding(32, 28, 32, 28) }
        tvShizukuStatus = TextView(this).apply { textSize = 15f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 20) }; updateShizukuUI(); card.addView(tvShizukuStatus)
        card.addView(Button(this).apply {
            text = if (ShizukuHelper.isShizukuInstalled(this@MainActivity)) "HUBUNGKAN / MINTA IZIN SHIZUKU" else "INSTALL SHIZUKU DI PLAY STORE"
            background = box("#E50914", "#FF3B30", 18f); setTextColor(Color.WHITE)
            setOnClickListener {
                if (!ShizukuHelper.isShizukuInstalled(this@MainActivity)) ShizukuHelper.openShizukuInPlayStore(this@MainActivity)
                else if (!ShizukuHelper.isShizukuRunning()) { ShizukuHelper.openShizukuApp(this@MainActivity); Toast.makeText(this@MainActivity, "Jalankan Shizuku terlebih dahulu", Toast.LENGTH_LONG).show() }
                else ShizukuHelper.requestShizukuPermission()
            }
        })
        card.addView(TextView(this).apply { text = "\nPerangkat: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\nABI: ${Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"} | Build: ${Build.ID}"; textSize = 13f; setTextColor(Color.LTGRAY); setPadding(0, 24, 0, 12) })
        layout.addView(card)
        if (AuthManager.currentUser?.role == UserRole.DEV) {
            val devCard = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E"); setPadding(32, 28, 32, 28); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 24, 0, 0) } }
            devCard.addView(TextView(this).apply { text = "PANEL KHUSUS DEV (KELOLA AKUN)"; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 12) })
            val tvList = TextView(this).apply { setTextColor(Color.WHITE); textSize = 13f; setPadding(0, 0, 0, 16) }
            fun refreshList() { tvList.text = AuthManager.getAllAccounts().joinToString("\n") { "• ${it.username} [${it.role.label}]" } }
            refreshList(); devCard.addView(tvList)
            val etU = EditText(this).apply { hint = "Username baru"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
            val etP = EditText(this).apply { hint = "Password baru"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
            val spRole = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, UserRole.values().map { it.label }) }
            devCard.addView(etU); devCard.addView(etP); devCard.addView(spRole)
            devCard.addView(Button(this).apply {
                text = "TAMBAH AKUN"; background = box("#E50914", "#FF3B30", 16f); setTextColor(Color.WHITE)
                setOnClickListener {
                    val role = UserRole.values()[spRole.selectedItemPosition]
                    if (AuthManager.addAccount(this@MainActivity, etU.text.toString(), etP.text.toString(), role)) { etU.text.clear(); etP.text.clear(); refreshList(); Toast.makeText(this@MainActivity, "Akun ditambahkan!", Toast.LENGTH_SHORT).show() }
                    else Toast.makeText(this@MainActivity, "Gagal / Username sudah ada", Toast.LENGTH_SHORT).show()
                }
            })
            layout.addView(devCard)
        }
        layout.addView(Button(this).apply { text = "LOGOUT"; setTextColor(Color.WHITE); background = box("#241014", "#4A1820", 18f); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 24, 0, 0) }; setOnClickListener { AuthManager.logout(); startActivity(Intent(this@MainActivity, LoginActivity::class.java)); finish() } })
        scroll.addView(layout); contentContainer.removeAllViews(); contentContainer.addView(scroll)
    }
    private fun updateShizukuUI() {
        if (!::tvShizukuStatus.isInitialized) return
        tvShizukuStatus.text = when {
            !ShizukuHelper.isShizukuInstalled(this) -> "Status Shizuku: Belum Terinstal"
            !ShizukuHelper.isShizukuRunning() -> "Status Shizuku: Terinstal, belum berjalan"
            !ShizukuHelper.hasShizukuPermission() -> "Status Shizuku: Berjalan (Belum diizinkan)"
            else -> "Status Shizuku: Terhubung & Siap (v13)"
        }
    }
    override fun onDestroy() { super.onDestroy(); wifiLock?.let { if (it.isHeld) it.release() }; Shizuku.removeBinderReceivedListener(binderListener); Shizuku.removeRequestPermissionResultListener(permListener) }
}
