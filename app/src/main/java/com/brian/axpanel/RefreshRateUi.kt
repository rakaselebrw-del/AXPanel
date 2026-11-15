package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.view.WindowManager; import android.widget.*
object RefreshRateUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        @Suppress("DEPRECATION") val disp = (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val curHz = disp.refreshRate.toInt()
        val maxHz = disp.supportedModes.maxOfOrNull { it.refreshRate.toInt() } ?: 120
        val modes = disp.supportedModes.map { it.refreshRate.toInt() }.distinct().sortedDescending().joinToString("Hz, ") + "Hz"
        card.addView(TextView(ctx).apply { text = "REFRESH RATE LOCKER (SHIZUKU)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Aktif: ${curHz}Hz • Support HP: $modes"; textSize = 11f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 14) })
        fun hzBtn(label: String, bg: String, cmd: String, msg: String) = Button(ctx).apply {
            text = label; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand(cmd) }.start(); Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
            }
        }
        card.addView(hzBtn("Kunci Max Layar (${maxHz}Hz)", "#E50914", "settings put system peak_refresh_rate ${maxHz}.0 && settings put system min_refresh_rate ${maxHz}.0 && settings put system user_refresh_rate ${maxHz}.0", "Refresh Rate dikunci ke ${maxHz}Hz!"))
        card.addView(hzBtn("Kunci 90 Hz (Jika Support)", "#2A0A10", "settings put system peak_refresh_rate 90.0 && settings put system min_refresh_rate 90.0 && settings put system user_refresh_rate 90.0", "Target 90Hz Aktif!"))
        card.addView(hzBtn("Reset ke Auto (Default HP)", "#0E0F14", "settings delete system peak_refresh_rate; settings delete system min_refresh_rate; settings delete system user_refresh_rate", "Refresh Rate kembali ke Auto!"))
        parent.addView(card)
    }
}
