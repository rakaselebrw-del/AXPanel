package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.os.*; import android.widget.*
object TsxModuleUi {
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#CC120E16", "#99FF2A32", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "TSX MODULE RUNNER (SHIZUKU)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Wajib ekstrak folder TSXFreeV1.0 ke /sdcard/TSXFreeV1.0/ sebelum memakai tombol ini."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        fun runTsx(label: String, arg: String, bg: String) = Button(ctx).apply {
            text = label; textSize = 12f; setTextColor(Color.WHITE); background = box(bg, "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 6, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) { Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                Toast.makeText(ctx, "Menjalankan $arg...", Toast.LENGTH_SHORT).show()
                Thread {
                    val ok = ShizukuHelper.runCommand("[ -f /sdcard/TSXFreeV1.0/run.sh ] && sh /sdcard/TSXFreeV1.0/run.sh $arg")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(ctx, if (ok) "$arg berhasil dijalankan!" else "Gagal! Pastikan /sdcard/TSXFreeV1.0/run.sh ada.", Toast.LENGTH_LONG).show()
                    }
                }.start()
            }
        }
        card.addView(runTsx("Pasang TweaksX-Y1 (No Reso / DPI)", "TweaksX-Y1", "#E50914"))
        card.addView(runTsx("Pasang TweaksX-X1 (With Reso)", "TweaksX-X1", "#2A0A10"))
        card.addView(runTsx("Pasang TweaksX-X2 (With Reso)", "TweaksX-X2", "#2A0A10"))
        card.addView(runTsx("Pasang TweaksX-X3 (With Reso)", "TweaksX-X3", "#2A0A10"))
        card.addView(runTsx("Copot / Reset TSX (TweaksX-RESET)", "TweaksX-RESET", "#0E0F14"))
        parent.addView(card)
    }
}
