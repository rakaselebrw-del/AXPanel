package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.net.Uri; import android.widget.*
object DevSupportCardUi {
    private fun box(bg: String, st: String, r: Float = 22f) = GradientDrawable().apply {
        setColor(Color.parseColor(bg)); cornerRadius = r; setStroke(2, Color.parseColor(st))
    }
    private fun openUrl(ctx: Context, url: String) {
        try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        catch (_: Exception) { Toast.makeText(ctx, "Tidak dapat membuka tautan", Toast.LENGTH_SHORT).show() }
    }
    fun build(ctx: Context, parent: LinearLayout) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; background = box("#15161E", "#D4AF37", 26f); setPadding(32, 26, 32, 26)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 22, 0, 0) }
        }
        card.addView(TextView(ctx).apply { text = "DEVELOPER & SUPPORT TEAM"; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.parseColor("#D4AF37")) })
        card.addView(TextView(ctx).apply { text = "Developer: Brian • Yan Deluxion"; textSize = 15f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); setPadding(0, 6, 0, 4) })
        card.addView(TextView(ctx).apply { text = "Hubungi WhatsApp atau ikuti TikTok resmi untuk update & lisensi akun."; textSize = 11f; setTextColor(Color.parseColor("#8E92A4")); setPadding(0, 0, 0, 14) })
        card.addView(Button(ctx).apply {
            text = "💬 WHATSAPP DEVELOPER (0851-9623-3387)"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#10381C", "#00E676", 18f)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(0, 4, 0, 10) }
            setOnClickListener { openUrl(ctx, "https://wa.me/6285196233387") }
        })
        card.addView(Button(ctx).apply {
            text = "🎵 TIKTOK OFFICIAL (@inibriann_88)"; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE); background = box("#2A0A10", "#FF3B30", 18f)
            setOnClickListener { openUrl(ctx, "https://www.tiktok.com/@inibriann_88?_r=1&_t=ZS-9Ba7EPCS84z") }
        })
        parent.addView(card)
    }
}
