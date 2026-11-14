package com.brian.axpanel
enum class UserRole(val label: String) { DEV("Dev"), OWNER("Owner"), PARTNER("Partner"), VIP("VIP"), RESELLER("Reseller"), MEMBER("Member") }
data class UserAccount(val username: String, val password: String, val role: UserRole)
object AuthManager {
    private val accounts = listOf(
        UserAccount("brian", "2011", UserRole.DEV),
        UserAccount("owner1", "1234", UserRole.OWNER),
        UserAccount("partner1", "1234", UserRole.PARTNER),
        UserAccount("vip1", "1234", UserRole.VIP),
        UserAccount("reseller1", "1234", UserRole.RESELLER),
        UserAccount("member1", "1234", UserRole.MEMBER)
    )
    var currentUser: UserAccount? = null; private set
    fun login(username: String, password: String): UserAccount? {
        val user = accounts.find { it.username.equals(username.trim(), ignoreCase = true) && it.password == password }
        currentUser = user; return user
    }
    fun logout() { currentUser = null }
}
