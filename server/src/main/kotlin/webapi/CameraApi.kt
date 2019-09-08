package webapi

import camera.CameraHandler
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("camera")
@Consumes(MediaType.APPLICATION_JSON)
class CameraApi @Inject constructor(private val cameraHandler: CameraHandler){

    @GET
    @Path("image")
    @Produces("image/jpg")
    fun getPhoto(): Response {
        val image = cameraHandler.takePhoto()
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        val imageData = byteArrayOutputStream.toByteArray()
        return Response.ok(imageData).build()
    }
}