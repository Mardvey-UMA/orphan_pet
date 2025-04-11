package ru.animaltracker.userservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class S3ClientConfig(
    @Value("\${s3.region}") val region: String,
    @Value("\${s3.s3Url}") val s3Url: String,
    @Value("\${s3.accessKeyId}") val accessKeyId: String,
    @Value("\${s3.secretAccessKey}") val secretAccessKey: String
) {
    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        val credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey)

        return S3AsyncClient.builder()
            .apply {
                if (s3Url.isNotEmpty()) {
                    endpointOverride(URI.create(s3Url))
                    forcePathStyle(true)
                }
            }
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(region))
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey)

        return S3Presigner.builder()
            .apply {
                if (s3Url.isNotEmpty()) {
                    endpointOverride(URI.create(s3Url))
                }
            }
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(region))
            .build()
    }
}