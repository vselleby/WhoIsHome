
//TODO: Remove usage of singleton.
class Context {
    var deviceHandler: DeviceHandler? = null

    companion object {
        val instance = Context()
    }
}
