package com.brian.axpanel
import android.app.*; import android.content.*; import android.content.pm.ServiceInfo; import android.graphics.*; import android.graphics.drawable.GradientDrawable
import android.os.*; import android.view.*; import android.widget.TextView; import androidx.core.app.NotificationCompat
import kotlin.math.abs
class TimerOverlayService : Service() {
    private lateinit var wm: WindowManager; private lateinit var tv: TextView; private lateinit var p: WindowManager.LayoutParams
    private val h = Handler(Looper.getMainLooper()); private var active = false; private var sec = 0
    companion object { var isRunning = false }
    private val tick = object : Runnable {
        override fun run() {
            if (!isRunning) return
            if (active) sec++
            tv.text = String.format("%02d:%02d", sec / 60, sec % 60)
            h.postDelayed(this, 1000)
        }
    }
    override fun onCreate() {
        super.onCreate(); isRunning = true; startFg()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val bg = GradientDrawable().apply { setColor(Color.parseColor("#CC140608")); cornerRadius = 20f; setStroke(2, Color.parseColor("#00D2FF")) }
        tv = TextView(this).apply { text = "00:00"; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = bg; setPadding(22, 10, 22, 10) }
        var sx = 0; var sy = 0; var tx = 0f; var ty = 0f; var moved = false; var downT = 0L
        tv.setOnTouchListener { _, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> { sx = p.x; sy = p.y; tx = ev.rawX; ty = ev.rawY; moved = false; downT = System.currentTimeMillis(); true }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (ev.rawX - tx).toInt(); val dy = (ev.rawY - ty).toInt()
                    if (abs(dx) > 10 || abs(dy) > 10) moved = true
                    p.x = sx + dx; p.y = sy + dy; wm.updateViewLayout(tv, p); true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        if (System.currentTimeMillis() - downT > 600) { active = false; sec = 0; tv.text = "00:00" }
                        else active = !active
                    }
                    true
                }
                else -> false
            }
        }
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        p = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 24; y = 90 }
        wm.addView(tv, p); h.post(tick)
    }
    override fun onDestroy() { super.onDestroy(); isRunning = false; h.removeCallbacks(tick); if (::tv.isInitialized) wm.removeView(tv) }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun startFg() {
        val ch = "ym_timer_ch"
        if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(ch, "YM Timer", NotificationManager.IMPORTANCE_LOW))
        val n = NotificationCompat.Builder(this, ch).setContentTitle("YM Timer Aktif").setSmallIcon(android.R.drawable.ic_menu_recent_history).build()
        if (Build.VERSION.SDK_INT >= 34) startForeground(4, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(4, n)
    }
}
