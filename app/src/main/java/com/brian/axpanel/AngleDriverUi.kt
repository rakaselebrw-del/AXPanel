package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.*; import android.widget.*
object AngleDriverUi {
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#CC120E16", "#99FF2A32", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "ANGLE GL DRIVER (FF BIASA & MAX)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Gunakan driver ANGLE (OpenGL-on-Vulkan) resmi Android untuk Free Fire Biasa (ffth) atau MAX."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        fun btn(t: String, bg: String, cmd: String, msg: String) = Button(ctx).apply {
            text = t; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, "#00D2FF", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                Thread { val ok = ShizukuHelper.runCommand(cmd); Handler(Looper.getMainLooper()).post { Toast.makeText(ctx, if (ok) msg else "Tidak didukung di HP ini", Toast.LENGTH_SHORT).show() } }.start()
            }
        }
        card.addView(btn("Aktifkan ANGLE Driver (FF Biasa / ffth)", "#E50914", "settings put global angle_gl_driver_selection_pkgs com.dts.freefireth && settings put global angle_gl_driver_selection_values angle && am force-stop com.dts.freefireth", "ANGLE Driver Aktif untuk FF Biasa!"))
        card.addView(btn("Aktifkan ANGLE Driver (FF MAX)", "#2A0A10", "settings put global angle_gl_driver_selection_pkgs com.dts.freefiremax && settings put global angle_gl_driver_selection_values angle && am force-stop com.dts.freefiremax", "ANGLE Driver Aktif untuk FF MAX!"))
        card.addView(btn("Aktifkan ANGLE (FF Biasa + FF MAX)", "#2A0A10", "settings put global angle_gl_driver_selection_pkgs com.dts.freefireth,com.dts.freefiremax && settings put global angle_gl_driver_selection_values angle,angle", "ANGLE Driver Aktif untuk Keduanya!"))
        card.addView(btn("Reset Driver ke Native (Default)", "#0E0F14", "settings delete global angle_gl_driver_selection_pkgs; settings delete global angle_gl_driver_selection_values", "Driver dikembalikan ke Native!"))
        parent.addView(card)
    }
}
