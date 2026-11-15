package com.brian.axpanel
import android.content.Context; import android.provider.Settings; import org.json.JSONObject
import java.net.HttpURLConnection; import java.net.URL
enum class UserRole(val label: String) { DEV("Dev"), OWNER("Owner"), PARTNER("Partner"), VIP("VIP"), RESELLER("Reseller"), MEMBER("Member") }
data class UserAccount(val username: String, val password: String, val role: UserRole, val hwid: String = "")
object AuthManager {
    const val DB_URL = "https://ym-panel-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val defaultAccounts = listOf(
        UserAccount("brian", "2011", UserRole.DEV), UserAccount("owner1", "1234", UserRole.OWNER),
        UserAccount("partner1", "1234", UserRole.PARTNER), UserAccount("vip1", "1234", UserRole.VIP),
        UserAccount("reseller1", "1234", UserRole.RESELLER), UserAccount("member1", "1234", UserRole.MEMBER)
    )
    private val accounts = defaultAccounts.toMutableList()
    var currentUser: UserAccount? = null; private set
    fun getHwid(ctx: Context): String {
        val raw = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID) ?: "00000000"
        val clean = raw.uppercase().take(8).padEnd(8, '0')
        return "HP-${clean.substring(0, 4)}-${clean.substring(4, 8)}"
    }
    fun creatableRoles(myRole: UserRole): List<UserRole> = UserRole.values().filter { it.ordinal > myRole.ordinal }
    fun init(ctx: Context) {
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("extra_accounts", emptySet()) ?: emptySet()
        accounts.clear(); accounts.addAll(defaultAccounts)
        saved.forEach { entry ->
            val p = entry.split("|")
            if (p.size >= 3) {
                val role = UserRole.values().find { it.name == p[2] } ?: UserRole.MEMBER
                if (accounts.none { it.username.equals(p[0], true) }) accounts.add(UserAccount(p[0], p[1], role))
            }
        }
    }
    fun restoreSession(ctx: Context): Boolean {
        init(ctx)
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val u = prefs.getString("session_user", null) ?: return false
        val p = prefs.getString("session_pass", null) ?: return false
        val rName = prefs.getString("session_role", "MEMBER") ?: "MEMBER"
        val role = UserRole.values().find { it.name == rName } ?: UserRole.MEMBER
        currentUser = UserAccount(u, p, role, getHwid(ctx)); return true
    }
    private fun saveSession(ctx: Context, u: String, p: String, r: UserRole) {
        ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE).edit()
            .putString("session_user", u).putString("session_pass", p).putString("session_role", r.name).apply()
    }
    fun addAccount(ctx: Context, user: String, pass: String, role: UserRole): Pair<Boolean, String> {
        val u = user.trim().lowercase()
        if (u.isBlank() || pass.isBlank()) return false to "Username/password kosong!"
        if (accounts.any { it.username.equals(u, true) }) return false to "Username sudah ada!"
        val acc = UserAccount(u, pass, role); accounts.add(acc)
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val set = (prefs.getStringSet("extra_accounts", emptySet()) ?: emptySet()).toMutableSet()
        set.add("${acc.username}|${acc.password}|${acc.role.name}"); prefs.edit().putStringSet("extra_accounts", set).apply()
        if (DB_URL.isNotBlank()) {
            try {
                val conn = (URL("${DB_URL.trimEnd('/')}/accounts/$u.json").openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"; doOutput = true; setRequestProperty("Content-Type", "application/json")
                }
                val json = JSONObject().put("password", pass).put("role", role.name).put("hwid", "")
                conn.outputStream.use { it.write(json.toString().toByteArray()) }
                if (conn.responseCode !in 200..299) return false to "Gagal simpan ke server (${conn.responseCode})"
            } catch (e: Exception) { return false to "Error jaringan: ${e.message}" }
        }
        return true to "Akun $u [${role.label}] berhasil dibuat!"
    }
    fun getAllAccounts(): List<UserAccount> = accounts
    fun login(ctx: Context, username: String, password: String): Pair<UserAccount?, String> {
        init(ctx)
        val u = username.trim().lowercase(); val hwid = getHwid(ctx)
        if (DB_URL.isNotBlank()) {
            try {
                val conn = URL("${DB_URL.trimEnd('/')}/accounts/$u.json").openConnection() as HttpURLConnection
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().readText()
                    if (body != "null" && body.isNotBlank()) {
                        val obj = JSONObject(body)
                        if (obj.optString("password") != password) return null to "Password salah!"
                        val role = UserRole.values().find { it.name == obj.optString("role") } ?: UserRole.MEMBER
                        val savedHwid = obj.optString("hwid", "")
                        if (role != UserRole.DEV && savedHwid.isNotBlank() && savedHwid != hwid) return null to "Akun terkunci di HP lain ($savedHwid)!"
                        if (savedHwid.isBlank()) {
                            val put = (URL("${DB_URL.trimEnd('/')}/accounts/$u/hwid.json").openConnection() as HttpURLConnection).apply {
                                requestMethod = "PUT"; doOutput = true; setRequestProperty("Content-Type", "application/json")
                            }
                            put.outputStream.use { it.write("\"$hwid\"".toByteArray()) }; put.responseCode
                        }
                        val acc = UserAccount(u, password, role, hwid); currentUser = acc; saveSession(ctx, u, password, role); return acc to "OK"
                    }
                }
            } catch (_: Exception) {}
        }
        val user = accounts.find { it.username.equals(u, true) && it.password == password } ?: return null to "Username atau password salah!"
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val boundHwid = prefs.getString("hwid_$u", null)
        if (user.role != UserRole.DEV && boundHwid != null && boundHwid != hwid) return null to "Akun terkunci di HP lain ($boundHwid)!"
        prefs.edit().putString("hwid_$u", hwid).apply(); currentUser = user; saveSession(ctx, u, password, user.role); return user to "OK"
    }
    fun logout(ctx: Context? = null) {
        currentUser = null
        ctx?.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)?.edit()
            ?.remove("session_user")?.remove("session_pass")?.remove("session_role")?.apply()
    }
}
