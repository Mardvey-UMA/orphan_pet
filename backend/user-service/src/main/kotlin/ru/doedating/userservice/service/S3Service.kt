package ru.doedating.userservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

@Service
class S3Service(
    private val s3Client: S3Client,
    @Value("\${s3.bucket}") private val bucketName: String
) {
    private val objectMapper = ObjectMapper()

    suspend fun uploadFile(key: String, file: MultipartFile): String {
        val metadata = mapOf(
            "originalFilename" to file.originalFilename,
            "contentType" to file.contentType,
            "size" to file.size.toString()
        )

        return s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .metadata(metadata)
                .build(),
            AsyncRequestBody.fromBytes(file.bytes)
        ).thenApply { key }.await()
    }

    suspend fun getFileUrl(key: String): String {
        return s3Client.getPresignedUrl(
            GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest { it.bucket(bucketName).key(key) }
                .build()
        ).toString()
    }

    suspend fun deleteFile(key: String) {
        s3Client.deleteObject { it.bucket(bucketName).key(key) }.await()
    }
}