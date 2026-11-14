package com.brian.axpanel
import android.content.Context; import android.content.Intent; import android.content.pm.PackageManager; import android.net.Uri; import rikka.shizuku.Shizuku
object ShizukuHelper {
    const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    const val REQUEST_CODE_SHIZUKU = 1001
    fun isShizukuInstalled(context: Context): Boolean = try { context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0); true } catch (e: Exception) { false }
    fun isShizukuRunning(): Boolean = try { Shizuku.pingBinder() } catch (e: Exception) { false }
    fun hasShizukuPermission(): Boolean = try { isShizukuRunning() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED } catch (e: Exception) { false }
    fun requestShizukuPermission() { if (isShizukuRunning() && !hasShizukuPermission()) Shizuku.requestPermission(REQUEST_CODE_SHIZUKU) }
    fun runCommand(cmd: String): Boolean {
        if (!hasShizukuPermission()) return false
        return try {
            val m = Shizuku::class.java.getDeclaredMethod("newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java)
            m.isAccessible = true
            val p = m.invoke(null, arrayOf("sh", "-c", cmd), null, null) as Process
            p.waitFor() == 0
        } catch (e: Exception) { false }
    }
    fun openShizukuInPlayStore(context: Context) {
        try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$SHIZUKU_PACKAGE")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$SHIZUKU_PACKAGE")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }
    fun openShizukuApp(context: Context) {
        val i = context.packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE)
        if (i != null) context.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) else openShizukuInPlayStore(context)
    }
}
