package ru.animaltracker.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import java.time.Duration

interface S3Service {
    suspend fun uploadFile(
        file: MultipartFile,
        prefix: String,
        metadata: Map<String, String> = emptyMap()
    ): String

    suspend fun generatePresignedUrl(objectKey: String, duration: Duration): String
    suspend fun generatePresignedUrl(objectKey: String): String
    suspend fun deleteFile(objectKey: String)
    suspend fun getFileMetadata(objectKey: String): Map<String, String>
}