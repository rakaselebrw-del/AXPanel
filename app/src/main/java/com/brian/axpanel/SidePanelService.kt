package com.brian.axpanel
import android.app.*; import android.content.*; import android.content.pm.ServiceInfo; import android.graphics.*; import android.graphics.drawable.GradientDrawable
import android.os.*; import android.view.*; import android.widget.*; import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat; import java.util.*; import kotlin.math.abs
class SidePanelService : Service() {
    private lateinit var wm: WindowManager; private lateinit var container: FrameLayout
    private lateinit var edgeBar: LinearLayout; private lateinit var hudView: LinearLayout; private lateinit var tvStats: TextView
    private lateinit var params: WindowManager.LayoutParams; private val handler = Handler(Looper.getMainLooper())
    private var savedX = 0; private var savedY = 300
    companion object { var isRunning = false }
    private val statsTask = object : Runnable {
        override fun run() {
            if (!isRunning) return
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
            val ramPct = (((mi.totalMem - mi.availMem) * 100) / mi.totalMem).toInt()
            val bat = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val temp = (bat?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
            @Suppress("DEPRECATION") val hz = wm.defaultDisplay.refreshRate.toInt()
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            tvStats.text = "$time  •  RAM $ramPct%  •  ${temp}°C  •  ${hz}Hz"
            handler.postDelayed(this, 2000)
        }
    }
    override fun onCreate() {
        super.onCreate(); isRunning = true; startFg()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        container = FrameLayout(this)
        val barBg = GradientDrawable().apply { setColor(Color.parseColor("#CC140608")); cornerRadius = 18f; setStroke(3, Color.parseColor("#FF1E27")) }
        edgeBar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; background = barBg
            layoutParams = FrameLayout.LayoutParams(28, 150)
            addView(View(this@SidePanelService).apply {
                setBackgroundColor(Color.parseColor("#FF2A32"))
                layoutParams = LinearLayout.LayoutParams(8, 70)
            })
        }
        var sx = 0; var sy = 0; var tx = 0f; var ty = 0f; var moved = false
        edgeBar.setOnTouchListener { _, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> { sx = params.x; sy = params.y; tx = ev.rawX; ty = ev.rawY; moved = false; true }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (ev.rawX - tx).toInt(); val dy = (ev.rawY - ty).toInt()
                    if (abs(dx) > 12 || abs(dy) > 12) moved = true
                    params.x = sx + dx; params.y = sy + dy; wm.updateViewLayout(container, params); true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) toggleHud(true)
                    else {
                        val sw = resources.displayMetrics.widthPixels
                        params.x = if (params.x + 14 < sw / 2) 0 else sw - 28
                        savedX = params.x; savedY = params.y; wm.updateViewLayout(container, params)
                    }
                    true
                }
                else -> false
            }
        }
        val built = SidePanelHud.build(this, { toggleHud(false) }, { stopSelf() }, { m -> handler.post { Toast.makeText(this, m, Toast.LENGTH_SHORT).show() } })
        hudView = built.first; tvStats = built.second; hudView.visibility = View.GONE
        container.addView(edgeBar); container.addView(hudView)
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = savedX; y = savedY }
        wm.addView(container, params); handler.post(statsTask)
    }
    private fun toggleHud(open: Boolean) {
        edgeBar.visibility = if (open) View.GONE else View.VISIBLE
        hudView.visibility = if (open) View.VISIBLE else View.GONE
        if (open) {
            params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL; params.x = 0; params.y = 40
            params.width = (resources.displayMetrics.widthPixels * 0.92).toInt()
        } else {
            params.gravity = Gravity.TOP or Gravity.START; params.x = savedX; params.y = savedY
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
        }
        wm.updateViewLayout(container, params)
    }
    override fun onDestroy() { super.onDestroy(); isRunning = false; handler.removeCallbacks(statsTask); if (::container.isInitialized) wm.removeView(container) }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun startFg() {
        val ch = "ym_panel_ch"
        if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(ch, "Yan Tools HUD", NotificationManager.IMPORTANCE_LOW))
        val n = NotificationCompat.Builder(this, ch).setContentTitle("Yan Tools HUD Aktif").setSmallIcon(android.R.drawable.ic_menu_compass).build()
        if (Build.VERSION.SDK_INT >= 34) startForeground(3, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(3, n)
    }
}
