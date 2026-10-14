package com.example.template.connector

import com.example.template.model.DownloadUrl
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.time.Duration

interface FileConnector {
    fun createDirectories(dir: String)

    fun save(file: MultipartFile, path: Path)
    fun save(bytes: ByteArray, path: Path)
    fun copy(source: Path, dest: Path)
    fun delete(path: Path): Boolean

    fun url(path: Path): String
    fun resource(path: Path): Resource
    fun downloadURL(path: Path, expiration: Duration = Duration.ofMinutes(5)): DownloadUrl
    fun attachmentURL(path: Path, expiration: Duration = Duration.ofMinutes(5)): DownloadUrl
}
