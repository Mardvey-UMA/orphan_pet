package ru.doedating.userservice.service.interfaces

import kotlinx.coroutines.flow.Flow
import ru.doedating.userservice.dto.FileDeleteResult
import ru.doedating.userservice.dto.FileResponseDto
import ru.doedating.userservice.dto.FileUploadDto

interface S3FileService {
    suspend fun uploadFile(bucketKey: String, file: FileUploadDto): FileResponseDto
    suspend fun getFileUrl(bucketKey: String): String
    suspend fun deleteFile(bucketKey: String)
    suspend fun batchDeleteFiles(bucketKeys: List<String>): Flow<FileDeleteResult>
}