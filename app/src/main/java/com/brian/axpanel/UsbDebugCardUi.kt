package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.*; import android.provider.Settings; import android.widget.*
object UsbDebugCardUi {
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#CC120E16", "#00D2FF", 26f); setPadding(32, 26, 32, 26)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 22, 0, 0) }
        }
        card.addView(TextView(ctx).apply { text = "USB DEBUGGING & SHIZUKU SETUP"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 8) })
        val tvStat = TextView(ctx).apply { textSize = 12f; setTextColor(Color.WHITE); setPadding(0, 0, 0, 12) }
        fun refresh() {
            val dev = if (ShizukuHelper.isDevOptionsOn(ctx)) "AKTIF ●" else "MATI ○"
            val usb = if (ShizukuHelper.isUsbDebuggingOn(ctx)) "AKTIF ●" else "MATI ○"
            val wifi = if (ShizukuHelper.isWirelessDebuggingOn(ctx)) "AKTIF ●" else "MATI ○"
            val shi = if (ShizukuHelper.hasShizukuPermission()) "TERHUBUNG ●" else if (ShizukuHelper.isShizukuRunning()) "JALAN (BELUM IZIN)" else "MATI ○"
            tvStat.text = "• Opsi Pengembang : $dev\n• USB Debugging   : $usb\n• Wireless Debug  : $wifi\n• Shizuku Service : $shi"
        }
        refresh(); card.addView(tvStat)
        card.addView(Button(ctx).apply {
            text = "⚙ BUKA PENGATURAN USB DEBUGGING"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#2A0A10", "#D4AF37", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 4, 0, 8) }
            setOnClickListener {
                try { ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                catch (_: Exception) {
                    ctx.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    Toast.makeText(ctx, "Buka Tentang Ponsel > Ketuk Nomor Build 7x dulu!", Toast.LENGTH_LONG).show()
                }
            }
        })
        card.addView(Button(ctx).apply {
            text = "🧪 TES KONEKSI MODULE & REFRESH STATUS"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#0E0F14", "#00D2FF", 16f)
            setOnClickListener {
                refresh()
                Thread {
                    val (ok, msg) = ShizukuHelper.runCommandWithOutput("echo KONEKSI_AMAN")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(ctx, if (ok) "SUKSES! Semua module siap dipakai ($msg)" else "BELUM BISA: $msg", Toast.LENGTH_LONG).show()
                    }
                }.start()
            }
        })
        card.addView(TextView(ctx).apply { text = "\nCara agar semua module jalan:\n1. Klik Buka Pengaturan USB Debugging -> Nyalakan USB Debugging & Proses Debug Nirkabel.\n2. Buka aplikasi Shizuku -> Mulai.\n3. Kembali ke sini -> Klik Hubungkan Shizuku & Tes Koneksi."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")) })
        parent.addView(card)
    }
}
