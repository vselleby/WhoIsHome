package webapi
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory.createHttpServer
import javax.ws.rs.core.UriBuilder
import org.glassfish.grizzly.ssl.SSLContextConfigurator
import org.glassfish.grizzly.ssl.SSLEngineConfigurator
import org.glassfish.jersey.server.ResourceConfig


class WebServer {
    private val server: HttpServer

    init {
        val sslContext = SSLContextConfigurator()
        sslContext.setKeyStoreFile(KEYSTORE_SERVER_FILE)
        sslContext.setKeyStorePass(KEYSTORE_SERVER_PWD)
        sslContext.setTrustStoreFile(TRUSTORE_SERVER_FILE)
        sslContext.setTrustStorePass(TRUSTORE_SERVER_PWD)

        val resourceConfig = ResourceConfig().
            register(RestWebApi::class.java).
            packages("webapi")

        server = createHttpServer(
            UriBuilder.fromUri(HOST).port(PORT).build(),
            resourceConfig,
            true,
            SSLEngineConfigurator(sslContext).setClientMode(false).setNeedClientAuth(false)
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
        const val HOST = "https://localhost/api/"
        private const val KEYSTORE_SERVER_FILE = "/home/viktor/Documents/Programmering/WhoIsHome/keystore_server"
        private const val KEYSTORE_SERVER_PWD = "yourPassword"
        private const val TRUSTORE_SERVER_FILE = "/home/viktor/Documents/Programmering/WhoIsHome/truststore_server"
        private const val TRUSTORE_SERVER_PWD = "yourPassword"
    }
}
