package com.brian.axpanel
import android.app.*; import android.content.*; import android.content.pm.ServiceInfo; import android.graphics.*; import android.os.*; import android.view.*; import android.widget.*; import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat; import java.util.*
class SidePanelService : Service() {
    private lateinit var wm: WindowManager; private lateinit var container: FrameLayout
    private lateinit var btnLogo: ImageView; private lateinit var hudView: LinearLayout; private lateinit var tvStats: TextView
    private lateinit var params: WindowManager.LayoutParams; private val handler = Handler(Looper.getMainLooper())
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
        btnLogo = ImageView(this).apply {
            setImageResource(R.drawable.ym_logo); layoutParams = FrameLayout.LayoutParams(115, 115)
            setBackgroundColor(Color.parseColor("#CC1A0000")); setPadding(8, 8, 8, 8)
            setOnClickListener { toggleHud(true) }
        }
        val built = SidePanelHud.build(this, { toggleHud(false) }, { stopSelf() }, { m -> handler.post { Toast.makeText(this, m, Toast.LENGTH_SHORT).show() } })
        hudView = built.first; tvStats = built.second; hudView.visibility = View.GONE
        container.addView(btnLogo); container.addView(hudView)
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.START or Gravity.CENTER_VERTICAL; x = 0; y = 0 }
        wm.addView(container, params); handler.post(statsTask)
    }
    private fun toggleHud(open: Boolean) {
        btnLogo.visibility = if (open) View.GONE else View.VISIBLE
        hudView.visibility = if (open) View.VISIBLE else View.GONE
        params.gravity = if (open) (Gravity.TOP or Gravity.CENTER_HORIZONTAL) else (Gravity.START or Gravity.CENTER_VERTICAL)
        params.width = if (open) (resources.displayMetrics.widthPixels * 0.92).toInt() else WindowManager.LayoutParams.WRAP_CONTENT
        wm.updateViewLayout(container, params)
    }
    override fun onDestroy() { super.onDestroy(); isRunning = false; handler.removeCallbacks(statsTask); if (::container.isInitialized) wm.removeView(container) }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun startFg() {
        val ch = "ym_panel_ch"
        if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(ch, "YM Game HUD", NotificationManager.IMPORTANCE_LOW))
        val n = NotificationCompat.Builder(this, ch).setContentTitle("YM Game HUD Aktif").setSmallIcon(android.R.drawable.ic_menu_compass).build()
        if (Build.VERSION.SDK_INT >= 34) startForeground(3, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(3, n)
    }
}
