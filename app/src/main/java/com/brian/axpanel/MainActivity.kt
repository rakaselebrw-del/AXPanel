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
        if (AuthManager.currentUser == null) AuthManager.restoreSession(this)
        if (AuthManager.currentUser == null) AuthManager.restoreSession(this)
        val user = AuthManager.currentUser ?: run { startActivity(Intent(this, LoginActivity::class.java)); finish(); return }
        Shizuku.addBinderReceivedListener(binderListener); Shizuku.addRequestPermissionResultListener(permListener)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.parseColor("#0B0709")) }
        val topBar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; background = box("#15161E", "#2D181E", 0f); setPadding(36, 32, 36, 24); gravity = Gravity.CENTER_VERTICAL }
        topBar.addView(ImageView(this).apply { setImageResource(R.drawable.skeleton_king); layoutParams = LinearLayout.LayoutParams(92, 92).apply { setMargins(0, 0, 20, 0) } })
        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
        titleCol.addView(TextView(this).apply { text = "YAN DELUXION"; textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")) })
        titleCol.addView(TextView(this).apply { text = "${user.username} • ${AuthManager.getHwid(this@MainActivity)}"; textSize = 11f; setTextColor(Color.parseColor("#00D2FF")) })
        topBar.addView(titleCol)
        topBar.addView(TextView(this).apply { text = user.role.label.uppercase(); textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 18f); setPadding(28, 12, 28, 12) })
        contentContainer = FrameLayout(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f) }
        val bottomNav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setBackgroundColor(Color.parseColor("#13141B")); setPadding(16, 14, 16, 14) }
        fun navBtn(l: String, f: () -> Unit) = Button(this).apply { text = l; textSize = 13f; setTextColor(Color.WHITE); setBackgroundColor(Color.TRANSPARENT); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); setOnClickListener { f() } }
        ModernNavHelper.setup(this, bottomNav, { showHomeTab() }, { showFeatureTab() }, { showDeveloperTab() })
        val mid = FrameLayout(this).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f); addView(ImageView(this@MainActivity).apply { setImageResource(R.drawable.skeleton_king); alpha = 0.14f; scaleType = ImageView.ScaleType.FIT_CENTER }); addView(contentContainer.apply { layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT) }) }; root.addView(topBar); root.addView(mid); root.addView(bottomNav); setContentView(root); showHomeTab()
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
        contentContainer.alpha = 0f; contentContainer.animate().alpha(1f).setDuration(220).start(); contentContainer.removeAllViews(); contentContainer.addView(layout)
    }
    private fun showFeatureTab() {
        contentContainer.alpha = 0f; contentContainer.animate().alpha(1f).setDuration(220).start()
        contentContainer.removeAllViews(); contentContainer.addView(FeatureTabsUi.build(this))
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
        DevPanelUi.build(this, layout)
        layout.addView(Button(this).apply { text = "LOGOUT"; setTextColor(Color.WHITE); background = box("#241014", "#4A1820", 18f); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 24, 0, 0) }; setOnClickListener { AuthManager.logout(this@MainActivity); startActivity(Intent(this@MainActivity, LoginActivity::class.java)); finish() } })
        scroll.addView(layout); contentContainer.alpha = 0f; contentContainer.animate().alpha(1f).setDuration(220).start(); contentContainer.removeAllViews(); contentContainer.addView(scroll)
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
