package ru.doedating.userservice.dto

import org.springframework.web.multipart.MultipartFile

data class FileUploadDto(
    val file: ByteArray,
    val contentType: String,
    val originalFilename: String,
    val description: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FileUploadDto
        if (!file.contentEquals(other.file)) return false
        if (contentType != other.contentType) return false
        if (originalFilename != other.originalFilename) return false
        if (description != other.description) return false
        return true
    }

    override fun hashCode(): Int {
        var result = file.contentHashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + originalFilename.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}
