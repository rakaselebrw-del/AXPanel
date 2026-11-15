package com.brian.axpanel
import android.app.ActivityManager; import android.content.Context; import android.content.Intent; import android.content.IntentFilter
import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable; import android.os.*; import android.widget.*
import java.net.InetSocketAddress; import java.net.Socket
object ExtraToolsUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#262834", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "PING TESTER & HARDWARE INFO"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 10) })
        val tvPing = TextView(ctx).apply { text = "Ping Jaringan: Belum diuji"; textSize = 13f; setTextColor(Color.WHITE); setPadding(0, 0, 0, 12) }
        card.addView(tvPing)
        card.addView(Button(ctx).apply {
            text = "CEK PING / LATENCY SEKARANG"; textSize = 12f; setTextColor(Color.WHITE); background = box("#1A0B0E", "#00D2FF", 16f)
            setOnClickListener {
                tvPing.text = "Mengukur ping ke server..."
                Thread {
                    val res = try {
                        val t0 = System.currentTimeMillis()
                        Socket().use { it.connect(InetSocketAddress("1.1.1.1", 53), 2500) }
                        val ms = System.currentTimeMillis() - t0
                        val status = if (ms < 50) "Sangat Stabil (Hijau)" else if (ms < 100) "Stabil (Kuning)" else "Tinggi / Lag"
                        "Ping: ${ms} ms • $status"
                    } catch (e: Exception) { "Gagal mengukur ping (cek koneksi internet)" }
                    Handler(Looper.getMainLooper()).post { tvPing.text = res }
                }.start()
            }
        })
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
        val availMb = mi.availMem / (1024 * 1024); val totalMb = mi.totalMem / (1024 * 1024)
        val bat = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val lvl = bat?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val temp = (bat?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
        val volt = (bat?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0) / 1000f
        val cores = Runtime.getRuntime().availableProcessors()
        card.addView(TextView(ctx).apply {
            text = "\nCPU Cores: $cores Core\nRAM Tersedia: $availMb MB / $totalMb MB\nBaterai: $lvl% • Suhu: ${temp}°C • ${volt}V"
            textSize = 12f; setTextColor(Color.parseColor("#8E92A4"))
        })
        parent.addView(card)
    }
}
