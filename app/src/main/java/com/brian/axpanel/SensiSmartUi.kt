package com.brian.axpanel
import android.content.ClipData; import android.content.ClipboardManager; import android.content.Context
import android.graphics.Color; import android.graphics.Typeface; import android.graphics.drawable.GradientDrawable; import android.widget.*
object SensiSmartUi {
    private fun box(bg: String, stroke: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#CC120E16", "#99FF2A32", 26f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        val dpi = ctx.resources.displayMetrics.densityDpi
        card.addView(TextView(ctx).apply { text = "SENSI SMART GENERATOR (DPI: $dpi)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 0, 0, 6) })
        card.addView(TextView(ctx).apply { text = "Pilih gaya main untuk menghitung setelan sensivitas terbaik HP Anda:"; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 12) })
        val styles = listOf("One Tap / Shotgun", "Rush / SMG (MP40)", "All-Round / Ranked", "HP Kentang / Smooth")
        val spStyle = Spinner(ctx).apply { adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_dropdown_item, styles) }
        card.addView(spStyle)
        val tvResult = TextView(ctx).apply { textSize = 12f; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 14, 0, 14) }
        fun calc(idx: Int): String {
            val adj = if (dpi < 360) 6 else if (dpi > 480) -4 else 0
            val base = when (idx) {
                0 -> listOf(194, 186, 174, 160, 95, 150)
                1 -> listOf(188, 180, 176, 164, 110, 158)
                2 -> listOf(180, 172, 166, 154, 105, 145)
                else -> listOf(198, 192, 184, 172, 118, 168)
            }.map { (it + adj).coerceIn(100, 200) }
            return "Gaya: ${styles[idx]}\n• Lihat Sekeliling : ${base[0]}\n• Red Dot          : ${base[1]}\n• 2x Scope         : ${base[2]}\n• 4x Scope         : ${base[3]}\n• AWM Scope        : ${base[4]}\n• Lihat Sekitar    : ${base[5]}"
        }
        tvResult.text = calc(0)
        spStyle.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) { tvResult.text = calc(pos) }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }
        card.addView(tvResult)
        card.addView(Button(ctx).apply {
            text = "SALIN SETELAN SENSI"; textSize = 12f; setTextColor(Color.WHITE); background = box("#2A0A10", "#00D2FF", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 4, 0, 8) }
            setOnClickListener {
                val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("SensiSmart", tvResult.text.toString()))
                Toast.makeText(ctx, "Setelan Sensi Smart disalin!", Toast.LENGTH_SHORT).show()
            }
        })
        card.addView(Button(ctx).apply {
            text = "TERAPKAN FAST TOUCH RESPONSE (SHIZUKU)"; textSize = 12f; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 16f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 4, 0, 6) }
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku di tab Developer dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand("settings put system pointer_speed 5 && settings put secure long_press_timeout 250") }.start(); Toast.makeText(ctx, "Fast Touch Response Aktif!", Toast.LENGTH_SHORT).show() }
            }
        })
        card.addView(Button(ctx).apply {
            text = "Reset Touch Response Normal"; textSize = 11f; setTextColor(Color.WHITE); background = box("#0E0F14", "#66D4AF37", 16f)
            setOnClickListener {
                if (!ShizukuHelper.hasShizukuPermission()) Toast.makeText(ctx, "Hubungkan Shizuku dulu!", Toast.LENGTH_SHORT).show()
                else { Thread { ShizukuHelper.runCommand("settings put system pointer_speed 0 && settings put secure long_press_timeout 400") }.start(); Toast.makeText(ctx, "Touch Response dikembalikan ke Normal!", Toast.LENGTH_SHORT).show() }
            }
        })
        parent.addView(card)
    }
}
