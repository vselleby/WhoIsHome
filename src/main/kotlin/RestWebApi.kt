import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class RestWebApi {

    @GET
    @Produces("application/json")
    @Path("devices")
    fun list() : Array<String> {
        return arrayOf("a", "b", "c")
    }

}