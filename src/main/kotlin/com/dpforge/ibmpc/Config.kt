package com.dpforge.ibmpc

import java.io.File
import java.nio.charset.StandardCharsets
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Config private constructor(
    val biosROM: File,
    val floppyImage: File?,
    val cassetteBASICImages: List<File>?
) {
    companion object {

        private val json = Json {
            ignoreUnknownKeys = true
        }

        @Serializable
        private class Data(
            @SerialName("bios_rom") val biosROM: String,
            @SerialName("floppy_image") val floppyImage: String? = null,
            @SerialName("cassette_basic") val cassetteBASICImages: List<String>? = null,
        )

        fun read(file: File): Config {
            val content = file.readText(StandardCharsets.UTF_8)
            val data = json.decodeFromString<Data>(content)
            return Config(
                biosROM = data.biosROM.toFile(),
                floppyImage = data.floppyImage?.toFile(),
                cassetteBASICImages = data.cassetteBASICImages?.map { it.toFile() }
            )
        }

        private fun String.toFile(): File {
            val file = File(this)
            if (!file.exists()) {
                error("File '$this' not exits")
            }
            return file
        }
    }
}
