package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable; import android.widget.*
object LightModeUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "MODE RINGAN (DOWNSCALE GPU)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Turunkan resolusi sistem sementara via Shizuku agar GPU lebih enteng & FPS stabil."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        fun btn(label: String, bg: String, cmd: String, msg: String) = Button(ctx).apply {
            text = label; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand(cmd) }.start(); Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() }
            }
        }
        card.addView(btn("Mode Ringan (720p + Animasi 0x)", "#2A0A10", "wm size 720x1600 && settings put global window_animation_scale 0.0 && settings put global transition_animation_scale 0.0 && pm trim-caches 500M", "Mode Ringan 720p Aktif!"))
        card.addView(btn("Super Ringan (540p + Boost Max)", "#E50914", "wm size 540x1200 && settings put global window_animation_scale 0.0 && settings put global transition_animation_scale 0.0 && cmd power set-fixed-performance-mode-enabled true && am kill-all", "Mode Super Ringan 540p Aktif!"))
        card.addView(btn("Reset ke Asli (Resolusi Normal)", "#0E0F14", "wm size reset && wm density reset && settings put global window_animation_scale 1.0 && settings put global transition_animation_scale 1.0", "Layar dikembalikan ke Asli!"))
        parent.addView(card)
    }
}
