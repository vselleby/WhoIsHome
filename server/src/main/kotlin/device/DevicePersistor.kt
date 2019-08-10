package device

import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import com.google.gson.reflect.TypeToken



class DevicePersistor(private val deviceFilePath: String) {
    private val gson = Gson()

    fun save(devices: List<Device>) {
        val jsonString = gson.toJson(devices)
        FileWriter(File(deviceFilePath), false).write(jsonString)
    }

    fun load() : List<Device> {
        if (File(deviceFilePath).isFile) {
            return gson.fromJson(FileReader(File(deviceFilePath)), object : TypeToken<List<Device>>() {}.type)
        }
        return emptyList()
    }
}