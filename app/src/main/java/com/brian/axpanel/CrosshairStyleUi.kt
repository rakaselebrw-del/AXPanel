package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.graphics.Color; import android.graphics.drawable.GradientDrawable; import android.widget.*
object CrosshairStyleUi {
    fun build(ctx: Context, parent: LinearLayout) {
        parent.addView(TextView(ctx).apply { text = "Model Crosshair (6 Gaya)"; setTextColor(Color.parseColor("#FF3B30")); setPadding(0, 20, 0, 8) })
        val names = listOf("Plus (+)", "Dot (•)", "Circle (◯)", "Silang (X)", "T-Shape", "Scope")
        fun makeRow(indices: List<Int>): LinearLayout {
            val row = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0, 4, 0, 4) }
            indices.forEach { idx ->
                val bg = GradientDrawable().apply { setColor(Color.parseColor("#0E0F14")); cornerRadius = 14f; setStroke(2, Color.parseColor("#2D181E")) }
                row.addView(Button(ctx).apply {
                    text = names[idx]; textSize = 11f; setTextColor(Color.WHITE); background = bg
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(6, 0, 6, 0) }
                    setOnClickListener {
                        CrosshairOverlayService.crosshairStyle = idx
                        if (CrosshairOverlayService.isRunning) ctx.startService(Intent(ctx, CrosshairOverlayService::class.java).apply { action = CrosshairOverlayService.ACTION_UPDATE })
                        Toast.makeText(ctx, "Model: ${names[idx]}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            return row
        }
        parent.addView(makeRow(listOf(0, 1, 2)))
        parent.addView(makeRow(listOf(3, 4, 5)))
    }
}
