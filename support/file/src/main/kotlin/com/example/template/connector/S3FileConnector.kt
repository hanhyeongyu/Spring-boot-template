package com.example.template.connector

import com.example.template.model.DownloadUrl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.Resource
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.nio.file.Path
import java.net.URI
import java.time.Duration

@Service
@ConditionalOnProperty(name = ["file.storage.type"], havingValue = "s3")
class S3FileConnector(
    @Value("\${file.storage.endpoint}") private val endpoint: String,
    @Value("\${file.storage.bucket}") private val bucket: String,
    @Value("\${file.storage.access-key}") private val accessKey: String,
    @Value("\${file.storage.secret-key}") private val secretKey: String,
    @Value("\${file.storage.region}") private val region: String,
) : FileConnector {
    private val log = LoggerFactory.getLogger(javaClass)

    private val s3Client: S3Client by lazy {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)
        S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(region))
            .build()
    }

    private val s3Presigner: S3Presigner by lazy {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)
        S3Presigner.builder()
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(region))
            .build()
    }

    override fun createDirectories(dir: String) {
        // Object storage does not require directory creation.
    }

    override fun save(file: MultipartFile, path: Path) {
        save(file.bytes, path)
    }

    override fun save(bytes: ByteArray, path: Path) {
        val key = keyOf(path)
        log.info("S3 save - bucket: $bucket, key: $key")
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        val requestBody = RequestBody.fromBytes(bytes)
        s3Client.putObject(putObjectRequest, requestBody)
    }

    override fun copy(source: Path, dest: Path) {
        val sourceKey = keyOf(source)
        val destKey = keyOf(dest)
        log.info("S3 copy - bucket: $bucket, $sourceKey -> $destKey")
        val request = CopyObjectRequest.builder()
            .sourceBucket(bucket)
            .sourceKey(sourceKey)
            .destinationBucket(bucket)
            .destinationKey(destKey)
            .build()
        s3Client.copyObject(request)
    }

    override fun delete(path: Path): Boolean {
        return try {
            val key = keyOf(path)
            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()

            s3Client.deleteObject(deleteRequest)
            true
        } catch (e: Exception) {
            log.warn("S3 delete failed - bucket: {}, path: {}", bucket, path, e)
            false
        }
    }

    override fun url(path: Path): String {
        val key = keyOf(path)
        return "$endpoint/$bucket/$key"
    }

    override fun downloadURL(path: Path, expirationSeconds: Long): DownloadUrl =
        presignedURL(path = path, expirationSeconds = expirationSeconds, isAttachment = false)

    override fun attachmentURL(path: Path, expirationSeconds: Long): DownloadUrl =
        presignedURL(path = path, expirationSeconds = expirationSeconds, isAttachment = true)

    private fun presignedURL(path: Path, expirationSeconds: Long, isAttachment: Boolean): DownloadUrl {
        val key = keyOf(path)
        log.info("Generate presigned URL - bucket: $bucket, key: $key, expiration: $expirationSeconds seconds")

        val getObjectRequestBuilder = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
        if (isAttachment) {
            getObjectRequestBuilder.responseContentDisposition("attachment; filename=\"${path.fileName}\"")
        }

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(expirationSeconds))
            .getObjectRequest(getObjectRequestBuilder.build())
            .build()

        val url = s3Presigner.presignGetObject(presignRequest).url().toString()
        return DownloadUrl(url = url, expiresIn = expirationSeconds)
    }

    override fun resource(path: Path): Resource {
        val key = keyOf(path)
        log.debug("S3 resource - bucket: {}, key: {}", bucket, key)
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        val s3Object = s3Client.getObject(getObjectRequest)
        return InputStreamResource(s3Object)
    }

    private fun keyOf(path: Path): String =
        path.toString().replace("\\", "/")
}
