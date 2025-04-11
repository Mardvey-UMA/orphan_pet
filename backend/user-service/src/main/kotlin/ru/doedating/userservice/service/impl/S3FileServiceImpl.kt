package ru.doedating.userservice.service.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.doedating.userservice.dto.FileDeleteResult
import ru.doedating.userservice.dto.FileResponseDto
import ru.doedating.userservice.dto.FileUploadDto
import ru.doedating.userservice.service.interfaces.S3FileService
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration
import java.time.LocalDate
import java.util.*

@Service
class S3FileServiceImpl(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Presigner: S3Presigner,
    @Value("\${s3.bucket}") private val bucketName: String
) : S3FileService {

    override suspend fun uploadFile(bucketKey: String, file: FileUploadDto): FileResponseDto {
        val metadata = mapOf(
            "original-filename" to file.originalFilename,
            "description" to file.description ?: "",
            "uploaded-at" to LocalDate.now().toString()
        )

        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(bucketKey)
            .contentType(file.contentType)
            .metadata(metadata)
            .build()

        val response = s3AsyncClient.putObject(
            request,
            AsyncRequestBody.fromBytes(file.file)
        ).await()

        return FileResponseDto(
            id = 0,
            url = getFileUrl(bucketKey),
            description = file.description,
            createdAt = LocalDate.now(),
            contentType = file.contentType,
            originalFilename = file.originalFilename
        )
    }

    override suspend fun getFileUrl(bucketKey: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(bucketKey)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(30))
            .getObjectRequest(getObjectRequest)
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }

    override suspend fun deleteFile(bucketKey: String) {
        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(bucketKey)
            .build()

        s3AsyncClient.deleteObject(deleteRequest).await()
    }

    override suspend fun batchDeleteFiles(bucketKeys: List<String>): Flow<FileDeleteResult> = flow {
        bucketKeys.forEach { key ->
            try {
                deleteFile(key)
                emit(FileDeleteResult(success = true))
            } catch (e: Exception) {
                emit(FileDeleteResult(success = false, message = e.message))
            }
        }
    }.flowOn(Dispatchers.IO)
}