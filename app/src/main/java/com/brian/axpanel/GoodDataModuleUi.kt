package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.*; import android.widget.*
object GoodDataModuleUi {
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#2D181E", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "GODDATA 1.3 MODULE (CLEAN)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Versi bersih tanpa link WhatsApp. Jalankan sebelum buka game."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        val applyCmd = listOf(
            "cmd power set-fixed-performance-mode-enabled true",
            "cmd power set-adaptive-power-saver-enabled false",
            "cmd power set-power-saver-exemption com.dts.freefireth true",
            "cmd power set-power-saver-exemption com.dts.freefiremax true",
            "device_config put game_overlay com.dts.freefireth mode=2,fps=90,minFps=60,framePacing=performance",
            "device_config put game_overlay com.dts.freefiremax mode=2,fps=90,minFps=60,framePacing=performance",
            "settings put global disable_window_blurs 1",
            "settings put global animator_duration_scale 0.4",
            "settings put global transition_animation_scale 0.4",
            "settings put global window_animation_scale 0.4",
            "settings put global force_gpu_rendering 1",
            "am force-stop com.facebook.katana",
            "am force-stop com.instagram.android",
            "am force-stop com.google.android.youtube",
            "am force-stop com.tiktok",
            "am force-stop com.spotify.music",
            "cmd notification post -S bigtext -t 'GODDATA 1.3' ym_goddata 'GodData 1.3 berhasil diterapkan!'"
        ).joinToString("; ")
        val resetCmd = listOf(
            "device_config delete game_overlay com.dts.freefireth",
            "device_config delete game_overlay com.dts.freefiremax",
            "cmd power set-fixed-performance-mode-enabled false",
            "settings put global disable_window_blurs 0",
            "settings put global force_gpu_rendering 0",
            "settings put global animator_duration_scale 1.0",
            "settings put global transition_animation_scale 1.0",
            "settings put global window_animation_scale 1.0"
        ).joinToString("; ")
        fun btn(t: String, bg: String, cmd: String, msg: String) = Button(ctx).apply {
            text = t; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                Toast.makeText(ctx, "Menerapkan...", Toast.LENGTH_SHORT).show()
                Thread { ShizukuHelper.runCommand(cmd); Handler(Looper.getMainLooper()).post { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show() } }.start()
            }
        }
        card.addView(btn("⚡ PASANG GODDATA 1.3 (NO RESO)", "#E50914", applyCmd, "GodData 1.3 Berhasil Diterapkan!"))
        card.addView(btn("↺ RESET GODDATA KE NORMAL", "#0E0F14", resetCmd, "GodData Direset ke Normal!"))
        parent.addView(card)
    }
}
