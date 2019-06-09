package webapi

import Context
import java.util.stream.Collectors
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("devices")
class RestWebApi {
    //TODO: Replace this with dependency injection if possible
    private val deviceHandler = Context.instance.deviceHandler

    @GET
    fun list() : List<String> {
        return deviceHandler?.connectedDevices?.
            stream()?.
            map { device ->
                device.toString()
            }?.
            collect(Collectors.toList()).
            orEmpty()
    }


}