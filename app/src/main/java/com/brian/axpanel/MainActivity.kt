package com.brian.axpanel
import android.content.Intent; import android.graphics.Color; import android.net.Uri; import android.os.Build; import android.os.Bundle; import android.provider.Settings; import android.view.*; import android.widget.*; import androidx.appcompat.app.AppCompatActivity; import rikka.shizuku.Shizuku
class MainActivity : AppCompatActivity() {
    private lateinit var contentContainer: FrameLayout; private lateinit var tvShizukuStatus: TextView
    private val binderListener = Shizuku.OnBinderReceivedListener { updateShizukuUI() }
    private val permListener = Shizuku.OnRequestPermissionResultListener { _, _ -> updateShizukuUI() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = AuthManager.currentUser ?: run { startActivity(Intent(this, LoginActivity::class.java)); finish(); return }
        Shizuku.addBinderReceivedListener(binderListener); Shizuku.addRequestPermissionResultListener(permListener)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0D151C")) }
        val topBar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(40, 40, 40, 24); gravity = Gravity.CENTER_VERTICAL }
        val tvHeader = TextView(this).apply { text = "AX  |  By ${user.username}"; textSize = 18f; setTextColor(Color.WHITE); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
        val tvRoleBadge = TextView(this).apply { text = user.role.label.uppercase(); setTextColor(Color.WHITE); setBackgroundColor(Color.parseColor("#0095D9")); setPadding(32, 12, 32, 12) }
        topBar.addView(tvHeader); topBar.addView(tvRoleBadge)
        contentContainer = FrameLayout(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val bottomNav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setBackgroundColor(Color.parseColor("#182028")); setPadding(16, 20, 16, 20) }
        fun navBtn(l: String, f: () -> Unit) = Button(this).apply { text = l; setTextColor(Color.WHITE); setBackgroundColor(Color.TRANSPARENT); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); setOnClickListener { f() } }
        bottomNav.addView(navBtn("Home") { showHomeTab() }); bottomNav.addView(navBtn("Feature") { showFeatureTab() }); bottomNav.addView(navBtn("Developer") { showDeveloperTab() })
        root.addView(topBar); root.addView(contentContainer); root.addView(bottomNav); setContentView(root); showHomeTab()
    }
    private fun showHomeTab() {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 24, 40, 24) }
        layout.addView(createGameCard("Free Fire", "com.dts.freefireth")); layout.addView(createGameCard("Free Fire MAX", "com.dts.freefiremax"))
        contentContainer.removeAllViews(); contentContainer.addView(layout)
    }
    private fun createGameCard(title: String, pkg: String): View {
        val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0F242D")); setPadding(36, 36, 36, 36); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 24) } }
        card.addView(TextView(this).apply { text = title; textSize = 18f; setTextColor(Color.WHITE) })
        card.addView(TextView(this).apply { text = pkg; textSize = 13f; setTextColor(Color.GRAY) })
        card.addView(Button(this).apply { text = "Buka Game"; setOnClickListener { val i = packageManager.getLaunchIntentForPackage(pkg); if (i != null) startActivity(i) else Toast.makeText(this@MainActivity, "$title belum terinstal", Toast.LENGTH_SHORT).show() } })
        return card
    }
    private fun showFeatureTab() {
        val scroll = ScrollView(this); val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 24, 40, 24) }
        val sw = Switch(this).apply {
            text = "Aktifkan Crosshair di Game"; setTextColor(Color.WHITE); isChecked = CrosshairOverlayService.isRunning
            setOnCheckedChangeListener { bv, chk ->
                if (chk) {
                    if (!Settings.canDrawOverlays(this@MainActivity)) { bv.isChecked = false; startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))); Toast.makeText(this@MainActivity, "Izinkan tampilkan di atas aplikasi lain", Toast.LENGTH_LONG).show() }
                    else { val svc = Intent(this@MainActivity, CrosshairOverlayService::class.java); if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(svc) else startService(svc) }
                } else stopService(Intent(this@MainActivity, CrosshairOverlayService::class.java))
            }
        }
        val tvSize = TextView(this).apply { text = "Size Crosshair: ${CrosshairOverlayService.crosshairSize}%"; setTextColor(Color.parseColor("#00A8E8")); setPadding(0, 32, 0, 8) }
        val seek = SeekBar(this).apply {
            max = 100; progress = CrosshairOverlayService.crosshairSize
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, p: Int, f: Boolean) { val v = if (p < 10) 10 else p; CrosshairOverlayService.crosshairSize = v; tvSize.text = "Size Crosshair: $v%"; updateOverlay() }
                override fun onStartTrackingTouch(sb: SeekBar?) {}; override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        val colorRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf(Color.RED, Color.GREEN, Color.YELLOW, Color.WHITE, Color.CYAN, Color.BLACK).forEach { col ->
            colorRow.addView(Button(this).apply { setBackgroundColor(col); layoutParams = LinearLayout.LayoutParams(0, 100, 1f).apply { setMargins(8, 0, 8, 0) }; setOnClickListener { CrosshairOverlayService.crosshairColor = col; updateOverlay() } })
        }
        layout.addView(sw); layout.addView(tvSize); layout.addView(seek); layout.addView(TextView(this).apply { text = "Warna Crosshair"; setTextColor(Color.parseColor("#00A8E8")); setPadding(0, 32, 0, 12) }); layout.addView(colorRow)
        scroll.addView(layout); contentContainer.removeAllViews(); contentContainer.addView(scroll)
    }
    private fun updateOverlay() { if (CrosshairOverlayService.isRunning) startService(Intent(this, CrosshairOverlayService::class.java).apply { action = CrosshairOverlayService.ACTION_UPDATE }) }
    private fun showDeveloperTab() {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 24, 40, 24) }
        tvShizukuStatus = TextView(this).apply { textSize = 16f; setTextColor(Color.WHITE); setPadding(0, 0, 0, 24) }; updateShizukuUI()
        val btnShizuku = Button(this).apply {
            text = if (ShizukuHelper.isShizukuInstalled(this@MainActivity)) "Hubungkan / Minta Izin Shizuku" else "Install Shizuku di Play Store"
            setBackgroundColor(Color.parseColor("#0095D9")); setTextColor(Color.WHITE)
            setOnClickListener {
                if (!ShizukuHelper.isShizukuInstalled(this@MainActivity)) ShizukuHelper.openShizukuInPlayStore(this@MainActivity)
                else if (!ShizukuHelper.isShizukuRunning()) { ShizukuHelper.openShizukuApp(this@MainActivity); Toast.makeText(this@MainActivity, "Jalankan Shizuku terlebih dahulu", Toast.LENGTH_LONG).show() }
                else ShizukuHelper.requestShizukuPermission()
            }
        }
        val info = TextView(this).apply { text = "\nNama Perangkat: ${Build.MANUFACTURER} ${Build.MODEL}\nVersi Android: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\nABI Support: ${Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"}\nBuild ID: ${Build.ID}"; textSize = 14f; setTextColor(Color.LTGRAY); setPadding(0, 32, 0, 32) }
        val btnLogout = Button(this).apply { text = "Logout"; setOnClickListener { AuthManager.logout(); startActivity(Intent(this@MainActivity, LoginActivity::class.java)); finish() } }
        layout.addView(tvShizukuStatus); layout.addView(btnShizuku); layout.addView(info); layout.addView(btnLogout); contentContainer.removeAllViews(); contentContainer.addView(layout)
    }
    private fun updateShizukuUI() {
        if (!::tvShizukuStatus.isInitialized) return
        tvShizukuStatus.text = when {
            !ShizukuHelper.isShizukuInstalled(this) -> "Status Shizuku: Belum Terinstal (Klik tombol di bawah)"
            !ShizukuHelper.isShizukuRunning() -> "Status Shizuku: Terinstal, tetapi belum berjalan"
            !ShizukuHelper.hasShizukuPermission() -> "Status Shizuku: Berjalan (Belum diberi izin)"
            else -> "Status Shizuku: Berjalan & Terhubung (v13)"
        }
    }
    override fun onDestroy() { super.onDestroy(); Shizuku.removeBinderReceivedListener(binderListener); Shizuku.removeRequestPermissionResultListener(permListener) }
}
