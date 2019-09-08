data class ServerConfig(
    val routerAddress: String,
    val routerUser: String,
    val routerPort: Int,
    val pathToSSHKey: String,
    val persistentDeviceFilePath: String,
    val trustStorePath: String,
    val trustStorePassword: String,
    val keyStorePath: String,
    val keyStorePassword: String
)