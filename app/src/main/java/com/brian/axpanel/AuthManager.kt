package com.brian.axpanel
import android.content.Context; import android.provider.Settings
enum class UserRole(val label: String) { DEV("Dev"), OWNER("Owner"), PARTNER("Partner"), VIP("VIP"), RESELLER("Reseller"), MEMBER("Member") }
data class UserAccount(val username: String, val password: String, val role: UserRole)
object AuthManager {
    private val defaultAccounts = listOf(
        UserAccount("brian", "2011", UserRole.DEV),
        UserAccount("owner1", "1234", UserRole.OWNER),
        UserAccount("partner1", "1234", UserRole.PARTNER),
        UserAccount("vip1", "1234", UserRole.VIP),
        UserAccount("reseller1", "1234", UserRole.RESELLER),
        UserAccount("member1", "1234", UserRole.MEMBER)
    )
    private val accounts = defaultAccounts.toMutableList()
    var currentUser: UserAccount? = null; private set
    fun getHwid(ctx: Context): String {
        val raw = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID) ?: "00000000"
        val clean = raw.uppercase().take(8).padEnd(8, '0')
        return "HP-${clean.substring(0, 4)}-${clean.substring(4, 8)}"
    }
    fun init(ctx: Context) {
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("extra_accounts", emptySet()) ?: emptySet()
        accounts.clear(); accounts.addAll(defaultAccounts)
        saved.forEach { entry ->
            val p = entry.split("|")
            if (p.size == 3) {
                val role = UserRole.values().find { it.name == p[2] } ?: UserRole.MEMBER
                if (accounts.none { it.username.equals(p[0], true) }) accounts.add(UserAccount(p[0], p[1], role))
            }
        }
    }
    fun addAccount(ctx: Context, user: String, pass: String, role: UserRole): Boolean {
        if (user.isBlank() || pass.isBlank() || accounts.any { it.username.equals(user.trim(), true) }) return false
        val acc = UserAccount(user.trim(), pass, role); accounts.add(acc)
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val set = (prefs.getStringSet("extra_accounts", emptySet()) ?: emptySet()).toMutableSet()
        set.add("${acc.username}|${acc.password}|${acc.role.name}")
        prefs.edit().putStringSet("extra_accounts", set).apply(); return true
    }
    fun getAllAccounts(): List<UserAccount> = accounts
    fun login(ctx: Context, username: String, password: String): Pair<UserAccount?, String> {
        init(ctx)
        val user = accounts.find { it.username.equals(username.trim(), true) && it.password == password }
            ?: return null to "Username atau password salah!"
        val prefs = ctx.getSharedPreferences("ax_prefs", Context.MODE_PRIVATE)
        val hwid = getHwid(ctx)
        val boundHwid = prefs.getString("hwid_${user.username.lowercase()}", null)
        if (user.role != UserRole.DEV && boundHwid != null && boundHwid != hwid) {
            return null to "Akun terkunci di HP lain ($boundHwid)!"
        }
        prefs.edit().putString("hwid_${user.username.lowercase()}", hwid).apply()
        currentUser = user; return user to "OK"
    }
    fun logout() { currentUser = null }
}
