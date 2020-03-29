package webapi
import camera.CameraHandler
import device.DeviceHandler
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory.createHttpServer
import javax.ws.rs.core.UriBuilder
import org.glassfish.grizzly.ssl.SSLContextConfigurator
import org.glassfish.grizzly.ssl.SSLEngineConfigurator
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig


class WebServer(
    deviceHandler: DeviceHandler,
    cameraHandler: CameraHandler,
    trustStorePath: String,
    trustStorePassword: String,
    keyStorePath: String,
    keyStorePassword: String
) {
    private val server: HttpServer

    init {
        val sslContext = SSLContextConfigurator().apply {
            setKeyStoreFile(keyStorePath)
            setKeyStorePass(keyStorePassword)
            setTrustStoreFile(trustStorePath)
            setTrustStorePass(trustStorePassword)
        }

        val resourceConfig = ResourceConfig().
            packages("webapi", "device", "camera").
            register(DeviceApi::class.java).
            register(CameraApi::class.java).
            register(object: AbstractBinder() {
                override fun configure() {
                    bind(deviceHandler).to(DeviceHandler::class.java)
                    bind(cameraHandler).to(CameraHandler::class.java)
                }
            })

        server = createHttpServer(
            UriBuilder.fromUri(HOST).port(PORT).build(),
            resourceConfig,
            true,
            SSLEngineConfigurator(sslContext).setClientMode(false).setNeedClientAuth(true)
        )
    }

    fun start() {
        server.start()
        Thread.currentThread().join()
    }

    fun stop() {
        server.shutdownNow()
    }

    companion object {
        const val PORT = 8448
        const val HOST = "https://0.0.0.0/api/"
    }
}
