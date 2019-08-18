package webapi

import Context
import com.fasterxml.jackson.annotation.JsonProperty
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import device.Device
import java.util.logging.Logger
import javax.ws.rs.NotFoundException
import javax.ws.rs.POST
import javax.ws.rs.PathParam


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("devices")
class RestWebApi {
    //TODO: Replace this with dependency injection if possible
    private val deviceHandler = Context.instance.deviceHandler
    private val logger = Logger.getLogger(RestWebApi::class.java.name)

    @GET
    @Path("ping")
    fun ping() : String {
        logger.info("Ping received")
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
    fun modifyDeivce(@PathParam("macAddress") macAddress: String, request: DeviceModificationRequest): Device {
        logger.info("DeviceModificationRequest with name:${request.name} and notificationEnabled:${request.notificationEnabled}")
        val immutableName = request.name
        if (!immutableName.isNullOrEmpty()) {
            deviceHandler?.setName(macAddress, immutableName)
        }

        val immutableNotification = request.notificationEnabled
        if (immutableNotification != null) {
            deviceHandler?.setNotification(macAddress, immutableNotification)
        }
        return deviceHandler?.getDevice(macAddress) ?: throw NotFoundException("Device with MacAddress: $macAddress not found")
    }
}

data class DeviceModificationRequest(@JsonProperty("name") var name: String?, @JsonProperty("notificationEnabled") var notificationEnabled: Boolean?)
