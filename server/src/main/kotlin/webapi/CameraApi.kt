package webapi

import camera.CameraHandler
import java.awt.image.BufferedImage
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("camera")
class CameraApi @Inject constructor(private val cameraHandler: CameraHandler){

    @Produces("image/jpg")
    @Path("image")
    @GET
    fun getPhoto(): BufferedImage {
        return cameraHandler.takePhoto()
    }
}