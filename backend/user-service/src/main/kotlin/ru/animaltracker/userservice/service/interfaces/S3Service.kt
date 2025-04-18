package ru.animaltracker.userservice.service.interfaces

import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.model.S3Object
import java.time.Duration

interface S3Service {
     fun uploadFile(
        file: MultipartFile,
        prefix: String,
        metadata: Map<String, String> = emptyMap()
    ): String

     fun generatePresignedUrl(objectKey: String, duration: Duration): String
     fun generatePresignedUrl(objectKey: String): String
     fun deleteFile(objectKey: String)
     fun getFileMetadata(objectKey: String): Map<String, String>
}