package ru.animaltracker.userservice.service.impl

import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.animaltracker.userservice.exception.FileProcessingException
import ru.animaltracker.userservice.exception.FileUploadException
import ru.animaltracker.userservice.service.interfaces.S3Service
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest
import java.time.Duration
import java.util.*

@Service
class S3ServiceImpl(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Presigner: S3Presigner,
    @Value("\${s3.bucket}") private val bucket: String
) : S3Service {

    companion object {
        private const val DEFAULT_PRESIGNED_URL_DURATION_MINUTES = 15L
    }

    override suspend fun uploadFile(
        file: MultipartFile,
        prefix: String,
        metadata: Map<String, String>
    ): String {
        val objectKey = "$prefix/${UUID.randomUUID()}_${file.originalFilename?.replace(" ", "_")}"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(objectKey)
            .contentType(file.contentType)
            .metadata(metadata)
            .build()

        try {
            s3AsyncClient.putObject(
                putObjectRequest,
                AsyncRequestBody.fromBytes(file.bytes)
            ).await()
            return objectKey
        } catch (e: Exception) {
            throw FileUploadException("Failed to upload file: ${e.message}")
        }
    }

    override suspend fun generatePresignedUrl(objectKey: String, duration: Duration): String {
        try {
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build()

            val presignedRequest = s3Presigner.presignGetObject { builder ->
                builder.getObjectRequest(getObjectRequest)
                    .signatureDuration(duration)
            }

            return presignedRequest.url().toString()
        } catch (e: Exception) {
            throw FileProcessingException("Failed to generate presigned URL: ${e.message}")
        }
    }

    override suspend fun generatePresignedUrl(objectKey: String): String {
        return generatePresignedUrl(objectKey, Duration.ofMinutes(DEFAULT_PRESIGNED_URL_DURATION_MINUTES))
    }

    override suspend fun deleteFile(objectKey: String) {
        s3AsyncClient.deleteObject {
            it.bucket(bucket).key(objectKey)
        }.await()
    }

    override suspend fun getFileMetadata(objectKey: String): Map<String, String> {
        val response = s3AsyncClient.headObject {
            it.bucket(bucket).key(objectKey)
        }.await()

        return response.metadata().mapValues { (_, value) ->
            value
        }
    }
}