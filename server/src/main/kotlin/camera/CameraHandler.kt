package camera

import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.imageio.ImageIO

class CameraHandler {
    private val logger = Logger.getLogger(CameraHandler::class.java.name)
    private val cameraSemaphore = Semaphore(1)

    @Synchronized fun takePhoto() : BufferedImage {
        try {
            cameraSemaphore.acquire()
            logger.fine("Aquired semaphore")
            val process = Runtime.getRuntime().exec("raspistill -w 640 -h 480 -t 1 -vf -hf  -o $REST_IMAGE_PATH")
            process.waitFor(5, TimeUnit.SECONDS)
            logger.info("Camera process finished with exit value: ${process.exitValue()}")
            return ImageIO.read(File(REST_IMAGE_PATH))
        }
        finally {
            cameraSemaphore.release()
            logger.fine("Released semaphore")
        }
    }

    companion object {
        const val REST_IMAGE_PATH = "/home/pi/wiscam/requested_snapshot.jpg"
    }
}