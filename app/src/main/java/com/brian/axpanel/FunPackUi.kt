package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.widget.*
object FunPackUi {
    private fun box(bg: String, stroke: String, r: Float = 24f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(stroke))
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#171822", "#2D181E", 28f); setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 20) }
        }
        card.addView(TextView(ctx).apply { text = "ENTERTAINMENT & LATIHAN (v8.0)"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#00D2FF")); setPadding(0, 0, 0, 10) })
        card.addView(Button(ctx).apply {
            text = "🎯 BUKA AIM WARMUP ARENA & TES HEADSET"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#E50914", "#FF3B30", 18f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 4, 0, 14) }
            setOnClickListener { ctx.startActivity(Intent(ctx, AimTrainerActivity::class.java)) }); card.addView(Button(ctx).apply { text = "🎯 LATIHAN FLICK AimTrainerActivity::class.java)) HEADSHOT (SKILL)"; textSize = 12f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#2A0A10", "#FF3B30", 18f); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 14) }; setOnClickListener { ctx.startActivity(Intent(ctx, DragPracticeActivity::class.java)) } }
        })
        val drops = listOf("Peak", "Clock Tower", "Bimasakti Strip", "Pochinok", "Factory", "Mill", "Observatory", "Brasilia")
        val weapons = listOf("Shotgun + SMG Only", "Sniper + Pistol Only", "AR + Gloo Wall Max", "Senjata Pertama yang Ditemukan", "SMG Rush Tanpa Scope", "One-Tap Desert Eagle / Woodpecker")
        val tvChal = TextView(ctx).apply { text = "Klik Acak untuk tantangan mabar seru!"; textSize = 12f; setTextColor(Color.WHITE); setPadding(0, 0, 0, 10) }
        card.addView(tvChal)
        card.addView(Button(ctx).apply {
            text = "🎲 ACAK TANTANGAN MABAR"; textSize = 12f; setTextColor(Color.WHITE); background = box("#2A0A10", "#00D2FF", 18f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 0, 0, 14) }
            setOnClickListener { tvChal.text = "📍 Turun: ${drops.random()}\n🔫 Tantangan: ${weapons.random()}" }
        })
        val tips = listOf(
            "💡 Selalu pakai headset saat ranked agar arah langkah kaki musuh terdengar jelas.",
            "💡 Jaga suhu HP di bawah 40°C supaya prosesor tidak mengalami thermal throttling (lag).",
            "💡 Latih refleks 1–2 menit di Aim Warmup Arena sebelum mulai push rank.",
            "💡 Atur posisi tombol tembak sedikit lebih rendah agar ruang geser ke atas lebih lega.",
            "💡 Gunakan WiFi Stabilizer saat sinyal kurang stabil agar ping tidak melonjak tiba-tiba."
        )
        val tvTip = TextView(ctx).apply { text = tips.random(); textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 4, 0, 8) }
        card.addView(tvTip)
        card.addView(Button(ctx).apply {
            text = "Ganti Tips Pro"; textSize = 11f; setTextColor(Color.WHITE); background = box("#101118", "#2B2D3D", 16f)
            setOnClickListener { tvTip.text = tips.random() }
        })
        parent.addView(card)
    }
}
