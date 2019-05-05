
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.servlet.WebappContext
import org.glassfish.jersey.servlet.ServletContainer


class WebServer {
    private val server: HttpServer

    init {
        val context = WebappContext("who-is-home-rest")

        //Web API
        val servlet = context.addServlet("Jersey", ServletContainer::class.java)
        servlet.setInitParameter("jersey.config.server.provider.packages", "webapi")
        servlet.addMapping("/api/*")

        server = HttpServer.createSimpleServer(null, PORT)
        context.deploy(server)
    }

    fun start() {
        server.start()
        Thread.currentThread().join()
    }

    fun stop() {
        server.shutdownNow()
    }

    companion object {
        private const val PORT = 8091
    }
}
