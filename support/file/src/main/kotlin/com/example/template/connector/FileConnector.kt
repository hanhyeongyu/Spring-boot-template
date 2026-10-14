package com.example.template.connector

import com.example.template.model.DownloadUrl
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface FileConnector {
    fun createDirectories(dir: String)

    fun save(file: MultipartFile, path: Path)
    fun save(bytes: ByteArray, path: Path)
    fun copy(source: Path, dest: Path)
    fun delete(path: Path): Boolean

    fun url(path: Path): String
    fun resource(path: Path): Resource
    fun downloadURL(path: Path, expirationSeconds: Long = 300): DownloadUrl
    fun attachmentURL(path: Path, expirationSeconds: Long = 300): DownloadUrl
}
