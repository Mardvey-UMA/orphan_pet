package ru.animaltracker.userservice.service.interfaces
import ru.animaltracker.userservice.dto.*
interface AnalyticsReportService {
    fun getAnimalAnalytics(animalId: Long): List<AnimalAnalyticsResponse>
    fun exportAnimalToPdf(username: String, animalId: Long): ByteArray
}