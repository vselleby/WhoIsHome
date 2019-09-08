package camera

import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.Semaphore
import javax.imageio.ImageIO

class CameraHandler {
    private val cameraSemaphore = Semaphore(1)

    @Synchronized fun takePhoto() : BufferedImage {
        try {
            cameraSemaphore.acquire()
            Runtime.getRuntime().exec("raspistill -w 640 -h 480 -t 1 -vf -hf  -o $REST_IMAGE_PATH")
            return ImageIO.read(File(REST_IMAGE_PATH))
        }
        finally {
            cameraSemaphore.release()
        }
    }

    companion object {
        const val REST_IMAGE_PATH = "~/wiscam/requested_snapshot.jpg"
    }
}