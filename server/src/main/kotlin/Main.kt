import camera.CameraHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import device.DeviceHandler
import device.DevicePersistor
import webapi.WebServer
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.Timer

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Failed to start: Requires path to server config json file. See whoishome-server-config.json")
        return
    }

    val serverConfig = parseServerConfig(args[0])

    val devicePersistor = DevicePersistor(serverConfig.persistentDeviceFilePath)
    val deviceHandler = DeviceHandler(devicePersistor)
    val cameraHandler = CameraHandler()
    deviceHandler.persistentDevices = devicePersistor.load().toHashSet()
    startSSHPoller(serverConfig.routerUser, serverConfig.pathToSSHKey, serverConfig.routerAddress, serverConfig.routerPort, deviceHandler)

    val webServer = WebServer(
        deviceHandler,
        cameraHandler,
        serverConfig.trustStorePath,
        serverConfig.trustStorePassword,
        serverConfig.keyStorePath,
        serverConfig.keyStorePassword
    )
    webServer.start()
    Runtime.getRuntime().addShutdownHook(Thread { webServer.stop() })
}

fun parseServerConfig(configFilePath: String): ServerConfig {
    val configFile = File(configFilePath)
    if (configFile.exists() and configFile.isFile) {
        return Gson().fromJson(FileReader(configFile), object : TypeToken<ServerConfig>() {}.type)
    }
    throw FileNotFoundException("Could not find the file: $configFilePath.")
}

fun startSSHPoller(user: String, keyPath: String, ipAddress: String, port: Int, deviceHandler: DeviceHandler) {
    val sshPoller = SSHPoller(user, keyPath, ipAddress, port, deviceHandler)
    sshPoller.initialise()
    Timer().scheduleAtFixedRate(sshPoller, 1000, 10000)
}
