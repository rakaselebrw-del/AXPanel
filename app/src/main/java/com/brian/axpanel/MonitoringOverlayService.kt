package com.brian.axpanel
import android.app.*; import android.content.*; import android.content.pm.ServiceInfo; import android.graphics.*; import android.os.*; import android.view.*; import android.widget.TextView; import androidx.core.app.NotificationCompat
class MonitoringOverlayService : Service() {
    private lateinit var wm: WindowManager; private lateinit var tvInfo: TextView
    private val handler = Handler(Looper.getMainLooper())
    companion object { var isRunning = false }
    private val updater = object : Runnable {
        override fun run() {
            if (!isRunning) return
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
            val usedMb = (mi.totalMem - mi.availMem) / (1024 * 1024)
            val totalMb = mi.totalMem / (1024 * 1024)
            val bat = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val temp = (bat?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
            @Suppress("DEPRECATION") val hz = wm.defaultDisplay.refreshRate.toInt()
            tvInfo.text = "AX MONITOR\nRAM: ${usedMb}/${totalMb} MB\nSUHU: ${temp}°C | ${hz}Hz"
            handler.postDelayed(this, 1500)
        }
    }
    override fun onCreate() {
        super.onCreate(); isRunning = true; startFg()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        tvInfo = TextView(this).apply { setTextColor(Color.parseColor("#00E5FF")); textSize = 12f; setBackgroundColor(Color.parseColor("#AA0D151C")); setPadding(20, 12, 20, 12) }
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        val p = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 24; y = 90 }
        wm.addView(tvInfo, p); handler.post(updater)
    }
    override fun onDestroy() { super.onDestroy(); isRunning = false; handler.removeCallbacks(updater); if (::tvInfo.isInitialized) wm.removeView(tvInfo) }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun startFg() {
        val ch = "ax_monitor_ch"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(ch, "Monitoring Overlay", NotificationManager.IMPORTANCE_LOW))
        val n = NotificationCompat.Builder(this, ch).setContentTitle("AX Monitor Aktif").setSmallIcon(android.R.drawable.ic_menu_info_details).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) startForeground(2, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(2, n)
    }
}
