package com.brian.axpanel
import android.content.Intent; import android.graphics.Color; import android.net.Uri; import android.os.Build; import android.os.Bundle; import android.provider.Settings; import android.view.*; import android.widget.*; import androidx.appcompat.app.AppCompatActivity; import rikka.shizuku.Shizuku
class MainActivity : AppCompatActivity() {
    private lateinit var contentContainer: FrameLayout; private lateinit var tvShizukuStatus: TextView
    private var featureSubTab = 0
    private val binderListener = Shizuku.OnBinderReceivedListener { updateShizukuUI() }
    private val permListener = Shizuku.OnRequestPermissionResultListener { _, _ -> updateShizukuUI() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = AuthManager.currentUser ?: run { startActivity(Intent(this, LoginActivity::class.java)); finish(); return }
        Shizuku.addBinderReceivedListener(binderListener); Shizuku.addRequestPermissionResultListener(permListener)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0D151C")) }
        val topBar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(40, 40, 40, 24); gravity = Gravity.CENTER_VERTICAL }
        topBar.addView(TextView(this).apply { text = "AX  |  By ${user.username}"; textSize = 18f; setTextColor(Color.WHITE); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) })
        topBar.addView(TextView(this).apply { text = user.role.label.uppercase(); setTextColor(Color.WHITE); setBackgroundColor(Color.parseColor("#0095D9")); setPadding(32, 12, 32, 12) })
        contentContainer = FrameLayout(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val bottomNav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setBackgroundColor(Color.parseColor("#182028")); setPadding(16, 20, 16, 20) }
        fun navBtn(l: String, f: () -> Unit) = Button(this).apply { text = l; setTextColor(Color.WHITE); setBackgroundColor(Color.TRANSPARENT); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); setOnClickListener { f() } }
        bottomNav.addView(navBtn("Home") { showHomeTab() }); bottomNav.addView(navBtn("Feature") { showFeatureTab() }); bottomNav.addView(navBtn("Developer") { showDeveloperTab() })
        root.addView(topBar); root.addView(contentContainer); root.addView(bottomNav); setContentView(root); showHomeTab()
    }
    private fun showHomeTab() {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 24, 40, 24) }
        listOf("Free Fire" to "com.dts.freefireth", "Free Fire MAX" to "com.dts.freefiremax").forEach { (t, pkg) ->
            val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0F242D")); setPadding(36, 36, 36, 36); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 24) } }
            card.addView(TextView(this).apply { text = t; textSize = 18f; setTextColor(Color.WHITE) })
            card.addView(TextView(this).apply { text = pkg; textSize = 13f; setTextColor(Color.GRAY) })
            card.addView(Button(this).apply { text = "Buka Game"; setOnClickListener { val i = packageManager.getLaunchIntentForPackage(pkg); if (i != null) startActivity(i) else Toast.makeText(this@MainActivity, "$t belum terinstal", Toast.LENGTH_SHORT).show() } })
            layout.addView(card)
        }
        contentContainer.removeAllViews(); contentContainer.addView(layout)
    }
    private fun showFeatureTab() {
        val scroll = ScrollView(this); val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 24, 40, 24) }
        val subRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 0, 0, 24) }
        fun subBtn(l: String, idx: Int) = Button(this).apply {
            text = l; setTextColor(Color.WHITE)
            setBackgroundColor(if (featureSubTab == idx) Color.parseColor("#0095D9") else Color.parseColor("#132A35"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(8, 0, 8, 0) }
            setOnClickListener { featureSubTab = idx; showFeatureTab() }
        }
        subRow.addView(subBtn("Tools", 0)); subRow.addView(subBtn("Settings", 1)); layout.addView(subRow)
        if (featureSubTab == 0) {
            fun shizukuSwitch(title: String, cmdOn: String, cmdOff: String) = Switch(this).apply {
                text = title; setTextColor(Color.WHITE); setPadding(0, 20, 0, 20)
                setOnCheckedChangeListener { bv, chk ->
                    if (!ShizukuHelper.hasShizukuPermission()) { bv.isChecked = false; Toast.makeText(this@MainActivity, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show() }
                    else { val ok = ShizukuHelper.runCommand(if (chk) cmdOn else cmdOff); Toast.makeText(this@MainActivity, if (ok) "$title diperbarui" else "Gagal menjalankan perintah", Toast.LENGTH_SHORT).show() }
                }
            }
            layout.addView(shizukuSwitch("Smooth Animation (0.5x)", "settings put global window_animation_scale 0.5 && settings put global transition_animation_scale 0.5", "settings put global window_animation_scale 1.0 && settings put global transition_animation_scale 1.0"))
            layout.addView(shizukuSwitch("Max Performance Mode", "cmd power set-fixed-performance-mode-enabled true && am kill-all", "cmd power set-fixed-performance-mode-enabled false"))
            layout.addView(Button(this).apply {
                text = "Bersihkan Cache Sistem (Shizuku)"; setBackgroundColor(Color.parseColor("#0F242D")); setTextColor(Color.parseColor("#00E5FF"))
                setOnClickListener {
                    if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(this@MainActivity, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
                    else { ShizukuHelper.runCommand("pm trim-caches 500M && am kill-all"); Toast.makeText(this@MainActivity, "Cache dibersihkan!", Toast.LENGTH_SHORT).show() }
                }
            })
        } else {
            fun overlaySwitch(title: String, running: Boolean, cls: Class<*>) = Switch(this).apply {
                text = title; setTextColor(Color.WHITE); isChecked = running; setPadding(0, 16, 0, 16)
                setOnCheckedChangeListener { bv, chk ->
                    if (chk) {
                        if (!Settings.canDrawOverlays(this@MainActivity)) { bv.isChecked = false; startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))); Toast.makeText(this@MainActivity, "Izinkan tampilkan di atas aplikasi lain", Toast.LENGTH_LONG).show() }
                        else { val svc = Intent(this@MainActivity, cls); if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(svc) else startService(svc) }
                    } else stopService(Intent(this@MainActivity, cls))
                }
            }
            layout.addView(overlaySwitch("Monitoring Performa (RAM/Suhu/Hz)", MonitoringOverlayService.isRunning, MonitoringOverlayService::class.java))
            layout.addView(overlaySwitch("Crosshair di Game", CrosshairOverlayService.isRunning, CrosshairOverlayService::class.java))
            val tvSize = TextView(this).apply { text = "Size Crosshair: ${CrosshairOverlayService.crosshairSize}%"; setTextColor(Color.parseColor("#00A8E8")); setPadding(0, 24, 0, 4) }
            layout.addView(tvSize)
            layout.addView(SeekBar(this).apply { max = 100; progress = CrosshairOverlayService.crosshairSize; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { val v = if (p < 10) 10 else p; CrosshairOverlayService.crosshairSize = v; tvSize.text = "Size Crosshair: $v%"; updateOverlay() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
            val tvPos = TextView(this).apply { text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; setTextColor(Color.parseColor("#00A8E8")); setPadding(0, 24, 0, 4) }
            layout.addView(tvPos)
            layout.addView(SeekBar(this).apply { max = 400; progress = CrosshairOverlayService.offsetX + 200; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { CrosshairOverlayService.offsetX = p - 200; tvPos.text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; updateOverlay() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
            layout.addView(SeekBar(this).apply { max = 400; progress = CrosshairOverlayService.offsetY + 200; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { CrosshairOverlayService.offsetY = p - 200; tvPos.text = "Posisi X: ${CrosshairOverlayService.offsetX} | Y: ${CrosshairOverlayService.offsetY}"; updateOverlay() }; override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {} }) })
            layout.addView(TextView(this).apply { text = "Warna Crosshair"; setTextColor(Color.parseColor("#00A8E8")); setPadding(0, 24, 0, 8) })
            val colorRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            listOf(Color.RED, Color.GREEN, Color.YELLOW, Color.WHITE, Color.CYAN, Color.BLACK).forEach { col ->
                colorRow.addView(Button(this).apply { setBackgroundColor(col); layoutParams = LinearLayout.LayoutParams(0, 90, 1f).apply { setMargins(8, 0, 8, 0) }; setOnClickListener { CrosshairOverlayService.crosshairColor = col; updateOverlay() } })
            }
            layout.addView(colorRow)
        }
        scroll.addView(layout); contentContainer.removeAllViews(); contentContainer.addView(scroll)
    }
    private fun updateOverlay() { if (CrosshairOverlayService.isRunning) startService(Intent(this, CrosshairOverlayService::class.java).apply { action = CrosshairOverlayService.ACTION_UPDATE }) }
    private fun showDeveloperTab() {
        val scroll = ScrollView(this); val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 24, 40, 24) }
        tvShizukuStatus = TextView(this).apply { textSize = 16f; setTextColor(Color.WHITE); setPadding(0, 0, 0, 20) }; updateShizukuUI()
        layout.addView(tvShizukuStatus)
        layout.addView(Button(this).apply {
            text = if (ShizukuHelper.isShizukuInstalled(this@MainActivity)) "Hubungkan / Minta Izin Shizuku" else "Install Shizuku di Play Store"
            setBackgroundColor(Color.parseColor("#0095D9")); setTextColor(Color.WHITE)
            setOnClickListener {
                if (!ShizukuHelper.isShizukuInstalled(this@MainActivity)) ShizukuHelper.openShizukuInPlayStore(this@MainActivity)
                else if (!ShizukuHelper.isShizukuRunning()) { ShizukuHelper.openShizukuApp(this@MainActivity); Toast.makeText(this@MainActivity, "Jalankan Shizuku terlebih dahulu", Toast.LENGTH_LONG).show() }
                else ShizukuHelper.requestShizukuPermission()
            }
        })
        layout.addView(TextView(this).apply { text = "\nPerangkat: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\nABI: ${Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"} | Build: ${Build.ID}"; textSize = 14f; setTextColor(Color.LTGRAY); setPadding(0, 24, 0, 24) })
        if (AuthManager.currentUser?.role == UserRole.DEV) {
            layout.addView(TextView(this).apply { text = "PANEL KHUSUS DEV (KELOLA AKUN)"; textSize = 15f; setTextColor(Color.parseColor("#00E5FF")); setPadding(0, 16, 0, 8) })
            val tvList = TextView(this).apply { setTextColor(Color.WHITE); textSize = 13f; setPadding(0, 0, 0, 16) }
            fun refreshList() { tvList.text = AuthManager.getAllAccounts().joinToString("\n") { "• ${it.username} [${it.role.label}]" } }
            refreshList(); layout.addView(tvList)
            val etU = EditText(this).apply { hint = "Username baru"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
            val etP = EditText(this).apply { hint = "Password baru"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) }
            val spRole = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, UserRole.values().map { it.label }) }
            layout.addView(etU); layout.addView(etP); layout.addView(spRole)
            layout.addView(Button(this).apply {
                text = "Tambah Akun"; setBackgroundColor(Color.parseColor("#0F242D")); setTextColor(Color.WHITE)
                setOnClickListener {
                    val role = UserRole.values()[spRole.selectedItemPosition]
                    if (AuthManager.addAccount(this@MainActivity, etU.text.toString(), etP.text.toString(), role)) { etU.text.clear(); etP.text.clear(); refreshList(); Toast.makeText(this@MainActivity, "Akun ditambahkan!", Toast.LENGTH_SHORT).show() }
                    else Toast.makeText(this@MainActivity, "Gagal / Username sudah ada", Toast.LENGTH_SHORT).show()
                }
            })
        }
        layout.addView(Button(this).apply { text = "Logout"; setOnClickListener { AuthManager.logout(); startActivity(Intent(this@MainActivity, LoginActivity::class.java)); finish() } })
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
    override fun onDestroy() { super.onDestroy(); Shizuku.removeBinderReceivedListener(binderListener); Shizuku.removeRequestPermissionResultListener(permListener) }
}
