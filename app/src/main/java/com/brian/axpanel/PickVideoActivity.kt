package com.brian.axpanel
import android.content.Intent; import android.os.Bundle; import android.widget.Toast; import androidx.appcompat.app.AppCompatActivity
class PickVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = "video/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        @Suppress("DEPRECATION") startActivityForResult(i, 99)
    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(req: Int, res: Int, data: Intent?) {
        super.onActivityResult(req, res, data)
        if (req == 99 && res == RESULT_OK && data?.data != null) {
            val uri = data.data!!
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            getSharedPreferences("ax_prefs", MODE_PRIVATE).edit().putString("intro_video_uri", uri.toString()).apply()
            Toast.makeText(this, "Video intro game berhasil dipasang!", Toast.LENGTH_SHORT).show()
        }
        finish()
    }
}
