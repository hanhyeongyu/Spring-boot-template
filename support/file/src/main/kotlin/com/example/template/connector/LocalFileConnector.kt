package com.example.template.connector

import com.example.template.model.DownloadUrl
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path


@Service
@ConditionalOnProperty(name = ["file.storage.type"], havingValue = "local", matchIfMissing = true)
class LocalFileConnector(
    @Value("\${file.baseUrl}")
    private val baseURL: String,
): FileConnector {

    override fun createDirectories(dir: String) {
        val path = Path(dir)
        Files.createDirectories(path)
    }

    override fun save(file: MultipartFile, path: Path) {
        file.transferTo(File(path.toUri()))
    }

    override fun save(bytes: ByteArray, path: Path) {
        Files.write(path, bytes)
    }

    override fun copy(source: Path, dest: Path) {
        Files.createDirectories(dest.parent)
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING)
    }

    override fun delete(path: Path): Boolean {
        val file = File(path.toString())
        return if (file.exists()) {
            file.delete()

        } else {
            false
        }
    }

    override fun url(path: Path): String {
        var result = ""
        result += baseURL
        result += path.toString()
        return result
    }


    override fun resource(path: Path): Resource {
        //return UrlResource("file:${path}")
        return FileSystemResource(path)
    }

    override fun downloadURL(path: Path, expirationSeconds: Long) = DownloadUrl(
        url = url(path),
        expiresIn = expirationSeconds,
    )

    override fun attachmentURL(path: Path, expirationSeconds: Long): DownloadUrl =
        downloadURL(path, expirationSeconds)
}
