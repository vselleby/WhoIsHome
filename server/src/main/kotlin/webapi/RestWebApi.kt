package webapi

import Context
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import device.Device
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.PathParam


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("devices")
class RestWebApi {
    //TODO: Replace this with dependency injection if possible
    private val deviceHandler = Context.instance.deviceHandler

    @GET
    @Path("ping")
    fun ping() : String {
        return "{\"timeStamp\":${System.currentTimeMillis()}}"
    }

    @GET
    fun list() : List<Device> {
        return deviceHandler?.connectedDevices?.toList() ?: emptyList()
    }

    @GET
    @Path("{macAddress}")
    fun getDevice(@PathParam("macAddress") macAddress: String) : Device? {
        return deviceHandler?.getDevice(macAddress)
    }

    @POST
    @Path("{macAddress}")
    fun modifyDeivce(@PathParam("macAddress") macAddress: String, @Valid request: DeviceModificationRequest) {
        if (!request.name.isNullOrEmpty()) {
            deviceHandler?.setName(macAddress, request.name)
        }

        if (request.notificationEnabled != null) {
            deviceHandler?.setNotification(macAddress, request.notificationEnabled)
        }
    }


}

class DeviceModificationRequest(val name: String?, val notificationEnabled: Boolean?)
