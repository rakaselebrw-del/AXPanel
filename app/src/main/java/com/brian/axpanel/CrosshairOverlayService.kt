package com.brian.axpanel
import android.app.*; import android.content.*; import android.content.pm.ServiceInfo; import android.graphics.*; import android.os.*; import android.view.*; import androidx.core.app.NotificationCompat
class CrosshairOverlayService : Service() {
    private lateinit var windowManager: WindowManager; private var crosshairView: CrosshairView? = null; private lateinit var params: WindowManager.LayoutParams
    companion object {
        const val ACTION_UPDATE = "ACTION_UPDATE_CROSSHAIR"
        var isRunning = false; var crosshairSize = 40; var crosshairColor = Color.RED; var offsetX = 0; var offsetY = 0; var crosshairStyle = 0
    }
    override fun onCreate() {
        super.onCreate(); isRunning = true; startForegroundNotification()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager; crosshairView = CrosshairView(this)
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.CENTER; x = offsetX; y = offsetY }
        windowManager.addView(crosshairView, params)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_UPDATE && crosshairView != null) { params.x = offsetX; params.y = offsetY; windowManager.updateViewLayout(crosshairView, params); crosshairView?.invalidate() }
        return START_STICKY
    }
    override fun onDestroy() { super.onDestroy(); isRunning = false; crosshairView?.let { windowManager.removeView(it) } }
    override fun onBind(intent: Intent?): IBinder? = null
    private fun startForegroundNotification() {
        val chId = "ax_crosshair_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(chId, "Crosshair Overlay", NotificationManager.IMPORTANCE_LOW))
        val notif = NotificationCompat.Builder(this, chId).setContentTitle("YM Crosshair Aktif").setContentText("Crosshair berjalan di layar").setSmallIcon(android.R.drawable.ic_menu_mylocation).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) startForeground(1, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE) else startForeground(1, notif)
    }
    inner class CrosshairView(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        override fun onMeasure(w: Int, h: Int) { val s = (crosshairSize * 2) + 24; setMeasuredDimension(s, s) }
        override fun onDraw(c: Canvas) {
            super.onDraw(c); paint.color = crosshairColor; paint.strokeWidth = 4f
            val cx = width / 2f; val cy = height / 2f; val len = crosshairSize.toFloat(); val gap = 8f
            when (crosshairStyle) {
                0 -> {
                    paint.style = Paint.Style.STROKE
                    c.drawLine(cx, cy - gap, cx, cy - len, paint); c.drawLine(cx, cy + gap, cx, cy + len, paint)
                    c.drawLine(cx - gap, cy, cx - len, cy, paint); c.drawLine(cx + gap, cy, cx + len, cy, paint)
                    paint.style = Paint.Style.FILL; c.drawCircle(cx, cy, 3f, paint)
                }
                1 -> {
                    paint.style = Paint.Style.FILL; c.drawCircle(cx, cy, (len * 0.22f).coerceAtLeast(4f), paint)
                    paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; c.drawCircle(cx, cy, len * 0.55f, paint)
                }
                2 -> {
                    paint.style = Paint.Style.STROKE; c.drawCircle(cx, cy, len * 0.65f, paint)
                    paint.style = Paint.Style.FILL; c.drawCircle(cx, cy, 3.5f, paint)
                }
                3 -> {
                    paint.style = Paint.Style.STROKE; val d = len * 0.72f; val dg = gap * 0.7f
                    c.drawLine(cx - dg, cy - dg, cx - d, cy - d, paint); c.drawLine(cx + dg, cy - dg, cx + d, cy - d, paint)
                    c.drawLine(cx - dg, cy + dg, cx - d, cy + d, paint); c.drawLine(cx + dg, cy + dg, cx + d, cy + d, paint)
                    paint.style = Paint.Style.FILL; c.drawCircle(cx, cy, 3f, paint)
                }
                4 -> {
                    paint.style = Paint.Style.STROKE
                    c.drawLine(cx, cy + gap, cx, cy + len, paint)
                    c.drawLine(cx - gap, cy, cx - len, cy, paint); c.drawLine(cx + gap, cy, cx + len, cy, paint)
                    paint.style = Paint.Style.FILL; c.drawCircle(cx, cy, 3.5f, paint)
                }
                5 -> {
                    paint.style = Paint.Style.STROKE; c.drawCircle(cx, cy, len * 0.6f, paint)
                    c.drawLine(cx, cy - gap, cx, cy - len, paint); c.drawLine(cx, cy + gap, cx, cy + len, paint)
                    c.drawLine(cx - gap, cy, cx - len, cy, paint); c.drawLine(cx + gap, cy, cx + len, cy, paint)
                    paint.style = Paint.Style.FILL; c.drawCircle(cx, cy, 3f, paint)
                }
            }
        }
    }
}
