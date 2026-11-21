package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable; import android.widget.*
object TouchDpiUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#CC120E16", "#99FF2A32", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        val baseDpi = ctx.resources.displayMetrics.densityDpi
        val d1 = (baseDpi * 0.85).toInt().coerceAtLeast(240)
        val d2 = (baseDpi * 0.75).toInt().coerceAtLeast(220)
        card.addView(TextView(ctx).apply { text = "LAYAR LICIN (DPI & TOUCH TUNER)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Atur DPI sistem (seperti Opsi Pengembang) & Pointer Speed agar swipe terasa lebih licin. DPI saat ini: $baseDpi"; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        fun btn(label: String, bg: String, cmd: String, msg: String) = Button(ctx).apply {
            text = label; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand(cmd) }.start(); Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
            }
        }
        card.addView(btn("Layar Licin (DPI $d1 + Pointer +5)", "#2A0A10", "wm density $d1 && settings put system pointer_speed 5 && settings put secure long_press_timeout 250", "Mode Layar Licin Aktif!"))
        card.addView(btn("Extra Licin (DPI $d2 + Pointer +7)", "#E50914", "wm density $d2 && settings put system pointer_speed 7 && settings put secure long_press_timeout 200", "Mode Extra Licin Aktif!"))
        card.addView(btn("Reset DPI & Sentuhan ke Normal", "#0E0F14", "wm density reset && settings put system pointer_speed 0 && settings put secure long_press_timeout 400", "DPI & Sentuhan kembali Normal!"))
        parent.addView(card)
    }
}
