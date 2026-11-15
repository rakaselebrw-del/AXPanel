package com.brian.axpanel
import android.content.Context; import android.graphics.Color; import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable; import android.widget.*
object ModernNavHelper {
    fun setup(ctx: Context, bar: LinearLayout, onHome: () -> Unit, onFeat: () -> Unit, onDev: () -> Unit) {
        bar.removeAllViews()
        bar.setBackgroundColor(Color.parseColor("#111219"))
        bar.setPadding(24, 16, 24, 18)
        val labels = listOf("⌂ Home", "⚡ Feature", "⚙ Developer")
        val actions = listOf(onHome, onFeat, onDev)
        val btns = mutableListOf<Button>()
        fun updateStyle(active: Int) {
            btns.forEachIndexed { idx, b ->
                if (idx == active) {
                    b.background = GradientDrawable().apply { setColor(Color.parseColor("#2A0C12")); cornerRadius = 40f; setStroke(2, Color.parseColor("#E50914")) }
                    b.setTextColor(Color.WHITE); b.typeface = Typeface.DEFAULT_BOLD
                } else {
                    b.setBackgroundColor(Color.TRANSPARENT); b.setTextColor(Color.parseColor("#8E92A4")); b.typeface = Typeface.DEFAULT
                }
            }
        }
        labels.forEachIndexed { idx, lbl ->
            val b = Button(ctx).apply {
                text = lbl; textSize = 12f; isAllCaps = false
                layoutParams = LinearLayout.LayoutParams(0, 96, 1f).apply { setMargins(8, 0, 8, 0) }
                setOnClickListener { updateStyle(idx); actions[idx]() }
            }
            btns.add(b); bar.addView(b)
        }
        updateStyle(0)
    }
}
