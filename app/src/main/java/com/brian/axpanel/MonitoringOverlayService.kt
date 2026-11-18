package com.brian.axpanel
import android.app.*; import android.content.*; import android.content.pm.ServiceInfo
import android.graphics.*; import android.graphics.drawable.GradientDrawable
import android.os.*; import android.view.*; import android.widget.*; import androidx.core.app.NotificationCompat
import kotlin.math.abs
class MonitoringOverlayService : Service() {
    private lateinit var wm: WindowManager; private lateinit var row: LinearLayout
    private lateinit var tvFps: TextView; private lateinit var tvRam: TextView
    private lateinit var tvTemp: TextView; private lateinit var tvBat: TextView
    private lateinit var p: WindowManager.LayoutParams
    private var frames = 0; private var fps = 60; private var lastT = 0L
    companion object { var isRunning = false }
    private val frameCb = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isRunning) return
            frames++; val now = System.currentTimeMillis()
            if (now - lastT >= 1000) { fps = frames; frames = 0; lastT = now; updateStats() }
            Choreographer.getInstance().postFrameCallback(this)
        }
    }
    private fun updateStats() {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
        val ramPct = (((mi.totalMem - mi.availMem) * 100) / mi.totalMem).toInt()
        val bat = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = (bat?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
        val lvl = bat?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
        tvFps.text = "FPS $fps"
        tvRam.text = "RAM $ramPct%"
        tvTemp.text = "${temp}°C"
        tvBat.text = "BAT $lvl%"
    }
    override fun onCreate() {
        super.onCreate(); isRunning = true; startFg()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val bg = GradientDrawable().apply { setColor(Color.parseColor("#DC0A0A0E")); cornerRadius = 16f; setStroke(2, Color.parseColor("#FF1E27")) }
        row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; background = bg; setPadding(14, 8, 18, 8) }
        row.addView(View(this).apply { setBackgroundColor(Color.parseColor("#FF1E27")); layoutParams = LinearLayout.LayoutParams(6, 30).apply { setMargins(0, 0, 12, 0) } })
        fun statTv(col: String) = TextView(this).apply {
            textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor(col)); setPadding(8, 0, 8, 0)
        }
        tvFps = statTv("#00E676"); tvRam = statTv("#00D2FF"); tvTemp = statTv("#FF9100"); tvBat = statTv("#FFFFFF")
        fun sep() = TextView(this).apply { text = "|"; textSize = 10f; setTextColor(Color.parseColor("#44475A")) }
        row.addView(tvFps); row.addView(sep()); row.addView(tvRam); row.addView(sep()); row.addView(tvTemp); row.addView(sep()); row.addView(tvBat)
        var sx = 0; var sy = 0; var tx = 0f; var ty = 0f
        row.setOnTouchListener { _, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> { sx = p.x; sy = p.y; tx = ev.rawX; ty = ev.rawY; true }
                MotionEvent.ACTION_MOVE -> { p.x = sx + (ev.rawX - tx).toInt(); p.y = sy + (ev.rawY - ty).toInt(); wm.updateViewLayout(row, p); true }
                else -> false
            }
        }
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        p = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; x = 0; y = 18 }
        wm.addView(row, p); updateStats(); lastT = System.currentTimeMillis(); Choreographer.getInstance().postFrameCallback(frameCb)
    }
    override fun onDestroy() { super.onDestroy(); isRunning = false; Choreographer.getInstance().removeFrameCallback(frameCb); if (::row.isInitialized) wm.removeView(row) }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun startFg() {
        val ch = "yan_monitor_ch"
        if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(ch, "Yan Tools Monitor", NotificationManager.IMPORTANCE_LOW))
        val n = NotificationCompat.Builder(this, ch).setContentTitle("ROG Monitor Aktif").setSmallIcon(android.R.drawable.ic_menu_info_details).build()
        if (Build.VERSION.SDK_INT >= 34) startForeground(2, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(2, n)
    }
}
