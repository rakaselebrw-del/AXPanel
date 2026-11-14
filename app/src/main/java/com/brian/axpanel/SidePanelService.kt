package com.brian.axpanel
import android.app.*; import android.content.*; import android.content.pm.ServiceInfo; import android.graphics.*; import android.net.wifi.WifiManager; import android.os.*; import android.view.*; import android.widget.*; import androidx.core.app.NotificationCompat
class SidePanelService : Service() {
    private lateinit var wm: WindowManager; private lateinit var container: LinearLayout
    private lateinit var menuLayout: LinearLayout; private var isExpanded = false; private var isRecording = false
    private var wifiLock: WifiManager.WifiLock? = null
    companion object { var isRunning = false }
    override fun onCreate() {
        super.onCreate(); isRunning = true; startFg()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        container = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val btnToggle = ImageView(this).apply {
            setImageResource(R.drawable.ym_logo); layoutParams = LinearLayout.LayoutParams(120, 120)
            setBackgroundColor(Color.parseColor("#CC1A0000")); setPadding(8, 8, 8, 8)
            setOnClickListener { isExpanded = !isExpanded; menuLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE }
        }
        menuLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; visibility = View.GONE
            setBackgroundColor(Color.parseColor("#E60D0608")); setPadding(24, 24, 24, 24)
        }
        menuLayout.addView(TextView(this).apply { text = "YM GAME PANEL"; textSize = 14f; setTextColor(Color.parseColor("#FF2A32")); setPadding(0, 0, 0, 12) })
        fun panelBtn(label: String, onClick: (Button) -> Unit) = Button(this).apply {
            text = label; textSize = 12f; setTextColor(Color.WHITE); setBackgroundColor(Color.parseColor("#2A080C"))
            layoutParams = LinearLayout.LayoutParams(360, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener { onClick(this) }
        }
        menuLayout.addView(panelBtn("Foto (Screenshot)") {
            if (!ShizukuHelper.hasShizukuPermission()) toast("Hubungkan Shizuku dulu!")
            else { Thread { ShizukuHelper.runCommand("mkdir -p /sdcard/Pictures && screencap -p /sdcard/Pictures/YM_${System.currentTimeMillis()}.png") }.start(); toast("Screenshot disimpan ke Pictures!") }
        })
        menuLayout.addView(panelBtn("Rekam Layar (Start)") { btn ->
            if (!ShizukuHelper.hasShizukuPermission()) { toast("Hubungkan Shizuku dulu!"); return@panelBtn }
            if (!isRecording) {
                isRecording = true; btn.text = "Stop Rekaman"
                Thread { ShizukuHelper.runCommand("mkdir -p /sdcard/Movies && nohup screenrecord /sdcard/Movies/YM_${System.currentTimeMillis()}.mp4 >/dev/null 2>&1 &") }.start()
                toast("Rekaman dimulai!")
            } else {
                isRecording = false; btn.text = "Rekam Layar (Start)"
                Thread { ShizukuHelper.runCommand("pkill -2 screenrecord") }.start()
                toast("Rekaman disimpan ke Movies!")
            }
        })
        menuLayout.addView(panelBtn("Game Booster") {
            if (!ShizukuHelper.hasShizukuPermission()) toast("Hubungkan Shizuku dulu!")
            else { Thread { ShizukuHelper.runCommand("pm trim-caches 500M && am kill-all && cmd power set-fixed-performance-mode-enabled true") }.start(); toast("Game Booster Aktif!") }
        })
        menuLayout.addView(panelBtn("WiFi Stabilizer") {
            val wmWifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            if (wifiLock == null) {
                @Suppress("DEPRECATION") val mode = if (Build.VERSION.SDK_INT >= 29) WifiManager.WIFI_MODE_FULL_LOW_LATENCY else WifiManager.WIFI_MODE_FULL_HIGH_PERF
                wifiLock = wmWifi.createWifiLock(mode, "YM_WifiLock").apply { acquire() }
                Thread { ShizukuHelper.runCommand("settings put global wifi_sleep_policy 2") }.start()
                toast("WiFi Stabilizer Aktif!")
            } else { wifiLock?.release(); wifiLock = null; toast("WiFi Stabilizer Nonaktif") }
        })
        menuLayout.addView(panelBtn("Toggle Crosshair") {
            val i = Intent(this, CrosshairOverlayService::class.java)
            if (CrosshairOverlayService.isRunning) stopService(i) else if (Build.VERSION.SDK_INT >= 26) startForegroundService(i) else startService(i)
        })
        menuLayout.addView(panelBtn("Toggle Monitor") {
            val i = Intent(this, MonitoringOverlayService::class.java)
            if (MonitoringOverlayService.isRunning) stopService(i) else if (Build.VERSION.SDK_INT >= 26) startForegroundService(i) else startService(i)
        })
        container.addView(btnToggle); container.addView(menuLayout)
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        val p = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.START or Gravity.CENTER_VERTICAL; x = 0; y = 0 }
        wm.addView(container, p)
    }
    private fun toast(m: String) = Handler(Looper.getMainLooper()).post { Toast.makeText(this, m, Toast.LENGTH_SHORT).show() }
    override fun onDestroy() { super.onDestroy(); isRunning = false; wifiLock?.let { if (it.isHeld) it.release() }; if (::container.isInitialized) wm.removeView(container) }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun startFg() {
        val ch = "ym_panel_ch"
        if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(ch, "YM Side Panel", NotificationManager.IMPORTANCE_LOW))
        val n = NotificationCompat.Builder(this, ch).setContentTitle("YM Game Panel Aktif").setSmallIcon(R.drawable.ym_logo).build()
        if (Build.VERSION.SDK_INT >= 34) startForeground(3, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(3, n)
    }
}
