package launcher.system

object SystemInfo {
    fun getUserHome(): String? {
        return System.getProperty("user.home")
    }
}
