package device

import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import com.google.gson.reflect.TypeToken
import java.util.logging.Logger


class DevicePersistor(private val deviceFilePath: String) {
    private val logger = Logger.getLogger(DevicePersistor::class.java.name)
    private val gson = Gson()

    fun save(devices: List<Device>) {
        val jsonString = gson.toJson(devices)
        logger.info("Saving to persistent storage file: $jsonString")
        with (FileWriter(File(deviceFilePath), false)) {
            write(jsonString)
            flush()
            close()
        }
    }

    fun load() : List<Device> {
        val deviceFile = File(deviceFilePath)
        if (deviceFile.isFile) {
            return gson.fromJson(FileReader(deviceFile), object : TypeToken<List<Device>>() {}.type)
        }
        return emptyList()
    }
}